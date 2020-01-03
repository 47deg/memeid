/*
 * Copyright 2019-2020 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package memeid.node

import java.net.{InetAddress, NetworkInterface}

import scala.collection.JavaConverters._

@SuppressWarnings(Array("scalafix:Disable.toString"))
private[node] object Sys {

  def getNetworkInterfaces: Set[String] = {
    val localHost     = InetAddress.getLocalHost
    val hostName      = localHost.getCanonicalHostName
    val baseAddresses = Set(localHost.toString, hostName)
    NetworkInterface.getNetworkInterfaces.asScala.foldLeft(baseAddresses)({
      case (addrs, ni) =>
        addrs ++ ni.getInetAddresses.asScala.map(_.toString).toSet
    })
  }

  def getLocalInterfaces: Set[String] = {
    val localHost = InetAddress.getLocalHost
    val hostName  = localHost.getCanonicalHostName
    InetAddress.getAllByName(hostName).map(_.toString).toSet
  }

  def getProperties: Map[String, String] = {
    val props = System.getProperties
    val keys  = props.stringPropertyNames
    keys.asScala.map(k => k -> props.getProperty(k)).toMap
  }
}
