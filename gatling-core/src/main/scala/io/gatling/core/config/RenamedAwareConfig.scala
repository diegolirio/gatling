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

package io.gatling.core.config

import java.time.{ Duration, Period }
import java.time.temporal.TemporalAmount
import java.lang.{ Boolean => JBoolean, Double => JDouble, Long => JLong }
import java.util.{ List => JList, Map => JMap, Set => JSet }
import java.util.concurrent.TimeUnit

import com.typesafe.config.{ Config, ConfigList, ConfigMemorySize, ConfigMergeable, ConfigObject, ConfigOrigin, ConfigResolveOptions, ConfigValue }

object RenamedAwareConfig {
  def apply(wrapped: Config, renamed: Seq[Renamed]): RenamedAwareConfig =
    new RenamedAwareConfig(wrapped, renamed.map(r => r.replacement -> r).toMap)
}

class RenamedAwareConfig(wrapped: Config, renamedByReplacement: Map[String, Renamed]) extends Config {

  private def revolvedPath(path: String): String =
    renamedByReplacement.get(path) match {
      case Some(replaced) if wrapped.hasPath(replaced.path) => replaced.path
      case _                                                => path
    }

  override def getInt(path: String): Int =
    wrapped.getInt(revolvedPath(path))

  override def getLong(path: String): Long =
    wrapped.getLong(revolvedPath(path))

  override def getDouble(path: String): Double =
    wrapped.getDouble(revolvedPath(path))

  override def getString(path: String): String =
    wrapped.getString(revolvedPath(path))

  override def getBoolean(path: String): Boolean =
    wrapped.getBoolean(revolvedPath(path))

  override def getStringList(path: String): JList[String] =
    wrapped.getStringList(revolvedPath(path))

  override def hasPath(path: String): Boolean =
    wrapped.hasPath(revolvedPath(path)) || wrapped.hasPath(path)

  override def root(): ConfigObject = throw new UnsupportedOperationException
  override def origin(): ConfigOrigin = throw new UnsupportedOperationException
  override def withFallback(other: ConfigMergeable): Config = throw new UnsupportedOperationException
  override def resolve(): Config = throw new UnsupportedOperationException
  override def resolve(options: ConfigResolveOptions): Config = throw new UnsupportedOperationException
  override def isResolved: Boolean = throw new UnsupportedOperationException
  override def resolveWith(source: Config): Config = throw new UnsupportedOperationException
  override def resolveWith(source: Config, options: ConfigResolveOptions): Config = throw new UnsupportedOperationException
  override def checkValid(reference: Config, restrictToPaths: String*): Unit = throw new UnsupportedOperationException
  override def hasPathOrNull(path: String): Boolean = throw new UnsupportedOperationException
  override def isEmpty: Boolean = throw new UnsupportedOperationException
  override def entrySet(): JSet[JMap.Entry[String, ConfigValue]] = throw new UnsupportedOperationException
  override def getIsNull(path: String): Boolean = throw new UnsupportedOperationException
  override def getNumber(path: String): Number = throw new UnsupportedOperationException
  override def getEnum[T <: Enum[T]](enumClass: Class[T], path: String): T = throw new UnsupportedOperationException
  override def getObject(path: String): ConfigObject = throw new UnsupportedOperationException
  override def getConfig(path: String): Config = throw new UnsupportedOperationException
  override def getAnyRef(path: String): AnyRef = throw new UnsupportedOperationException
  override def getValue(path: String): ConfigValue = throw new UnsupportedOperationException
  override def getBytes(path: String): JLong = throw new UnsupportedOperationException
  override def getMemorySize(path: String): ConfigMemorySize = throw new UnsupportedOperationException
  override def getMilliseconds(path: String): JLong = throw new UnsupportedOperationException
  override def getNanoseconds(path: String): JLong = throw new UnsupportedOperationException
  override def getDuration(path: String, unit: TimeUnit): Long = throw new UnsupportedOperationException
  override def getDuration(path: String): Duration = throw new UnsupportedOperationException
  override def getPeriod(path: String): Period = throw new UnsupportedOperationException
  override def getTemporal(path: String): TemporalAmount = throw new UnsupportedOperationException
  override def getList(path: String): ConfigList = throw new UnsupportedOperationException
  override def getBooleanList(path: String): JList[JBoolean] = throw new UnsupportedOperationException
  override def getNumberList(path: String): JList[Number] = throw new UnsupportedOperationException
  override def getIntList(path: String): JList[Integer] = throw new UnsupportedOperationException
  override def getLongList(path: String): JList[JLong] = throw new UnsupportedOperationException
  override def getDoubleList(path: String): JList[JDouble] = throw new UnsupportedOperationException
  override def getEnumList[T <: Enum[T]](enumClass: Class[T], path: String): JList[T] = throw new UnsupportedOperationException
  override def getObjectList(path: String): JList[_ <: ConfigObject] = throw new UnsupportedOperationException
  override def getConfigList(path: String): JList[_ <: Config] = throw new UnsupportedOperationException
  override def getAnyRefList(path: String): JList[_] = throw new UnsupportedOperationException
  override def getBytesList(path: String): JList[JLong] = throw new UnsupportedOperationException
  override def getMemorySizeList(path: String): JList[ConfigMemorySize] = throw new UnsupportedOperationException
  override def getMillisecondsList(path: String): JList[JLong] = throw new UnsupportedOperationException
  override def getNanosecondsList(path: String): JList[JLong] = throw new UnsupportedOperationException
  override def getDurationList(path: String, unit: TimeUnit): JList[JLong] = throw new UnsupportedOperationException
  override def getDurationList(path: String): JList[Duration] = throw new UnsupportedOperationException
  override def withOnlyPath(path: String): Config = throw new UnsupportedOperationException
  override def withoutPath(path: String): Config = throw new UnsupportedOperationException
  override def atPath(path: String): Config = throw new UnsupportedOperationException
  override def atKey(key: String): Config = throw new UnsupportedOperationException
  override def withValue(path: String, value: ConfigValue): Config = throw new UnsupportedOperationException
}
