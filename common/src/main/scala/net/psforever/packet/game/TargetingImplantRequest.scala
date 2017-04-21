// Copyright (c) 2017 PSForever
package net.psforever.packet.game

import net.psforever.packet.{GamePacketOpcode, Marshallable, PlanetSideGamePacket}
import scodec.Codec
import scodec.codecs._
import shapeless.{::, HNil}

final case class TargetRequest(target_guid : PlanetSideGUID,
                               unk : Boolean)

final case class TargetingImplantRequest(target_list : List[TargetRequest])
  extends PlanetSideGamePacket {
  type Packet = TargetingImplantRequest
  def opcode = GamePacketOpcode.TargetingImplantRequest
  def encode = TargetingImplantRequest.encode(this)
}

object TargetingImplantRequest extends Marshallable[TargetingImplantRequest] {
  private val request_codec : Codec[TargetRequest] = (
    ("target_guid" | PlanetSideGUID.codec) ::
      ("unk" | bool)
  ).xmap[TargetRequest] (
    {
      case a :: b :: HNil =>
        TargetRequest(a, b)
    },
    {
      case TargetRequest(a, b) =>
        a :: b :: HNil
    }
  )

  implicit val codec : Codec[TargetingImplantRequest] = ("target_list" | listOfN(intL(6), request_codec)).as[TargetingImplantRequest]
}
