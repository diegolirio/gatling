/*
 * Copyright 2011-2020 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.http.engine.response

import java.nio.charset.Charset

import io.gatling.commons.stats.{ KO, Status }
import io.gatling.commons.util.StringHelper.Eol
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.http.response.{ HttpResult, Response }
import io.gatling.http.util._
import io.gatling.netty.util.StringBuilderPool

import com.typesafe.scalalogging.StrictLogging

sealed abstract class StatsProcessor(charset: Charset) extends StrictLogging {
  def reportStats(
      fullRequestName: String,
      session: Session,
      status: Status,
      result: HttpResult,
      errorMessage: Option[String]
  ): Unit = {
    logTx(fullRequestName, session, status, result, errorMessage)
    reportStats0(fullRequestName, session, status, result, errorMessage)
  }

  protected def reportStats0(
      fullRequestName: String,
      session: Session,
      status: Status,
      result: HttpResult,
      errorMessage: Option[String]
  ): Unit

  private def logTx(
      fullRequestName: String,
      session: Session,
      status: Status,
      result: HttpResult,
      errorMessage: Option[String]
  ): Unit = {
    def dump = {
      // hack: pre-cache url because it would reset StringBuilderPool.DEFAULT otherwise
      // there's a good chance building the request only called toRelativeUrl
      result.request.getUri.toUrl
      StringBuilderPool.DEFAULT
        .get()
        .append(Eol)
        .appendWithEol(">>>>>>>>>>>>>>>>>>>>>>>>>>")
        .appendWithEol("Request:")
        .appendWithEol(s"$fullRequestName: $status ${errorMessage.getOrElse("")}")
        .appendWithEol("=========================")
        .appendWithEol("Session:")
        .append(session)
        .append(Eol)
        .appendWithEol("=========================")
        .appendWithEol("HTTP request:")
        .appendRequest(result, charset)
        .appendWithEol("=========================")
        .appendWithEol("HTTP response:")
        .appendResponse(result)
        .append("<<<<<<<<<<<<<<<<<<<<<<<<<")
        .toString
    }

    if (status == KO) {
      logger.info(s"Request '$fullRequestName' failed for user ${session.userId}: ${errorMessage.getOrElse("")}")
      if (!IsHttpTraceEnabled) {
        logger.debug(dump)
      }
    }

    logger.trace(dump)
  }
}

final class NoopStatsProcessor(charset: Charset) extends StatsProcessor(charset) {
  override protected def reportStats0(
      fullRequestName: String,
      session: Session,
      status: Status,
      result: HttpResult,
      errorMessage: Option[String]
  ): Unit = {}
}

final class DefaultStatsProcessor(
    charset: Charset,
    statsEngine: StatsEngine
) extends StatsProcessor(charset) {

  override def reportStats0(
      fullRequestName: String,
      session: Session,
      status: Status,
      result: HttpResult,
      errorMessage: Option[String]
  ): Unit =
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      fullRequestName,
      result.startTimestamp,
      result.endTimestamp,
      status,
      result match {
        case response: Response => Some(Integer.toString(response.status.code))
        case _                  => None
      },
      errorMessage
    )
}
