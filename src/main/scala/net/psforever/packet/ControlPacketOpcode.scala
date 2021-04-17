// Copyright (c) 2017 PSForever
package net.psforever.packet

import net.psforever.packet.control.{RelatedA, RelatedB, SlottedMetaPacket}
import scodec.bits.BitVector
import scodec.{Attempt, Codec, DecodeResult, Err}
import scodec.codecs._

import scala.annotation.switch

object ControlPacketOpcode extends Enumeration {
  type Type = Value
  val
  // OPCODES 0x00-0f
  HandleGamePacket,   // a whoopsi case: not actually a control packet, but a game packet
  ClientStart,        // first packet ever sent during client connection
  ServerStart,        // second packet sent in response to ClientStart
  MultiPacket,        // used to send multiple packets with one UDP message (subpackets limited to <= 255)
  Unknown4,           //
  TeardownConnection, //
  Unknown6,           //
  ControlSync,        // sent to the server from the client
  // 0x08
  ControlSyncResp,    // the response generated by the server
  SlottedMetaPacket0, //
  SlottedMetaPacket1, //
  SlottedMetaPacket2, //
  SlottedMetaPacket3, //
  SlottedMetaPacket4, //
  SlottedMetaPacket5, //
  SlottedMetaPacket6, //
  // OPCODES 0x10-1f
  SlottedMetaPacket7, //
  RelatedA0,          //
  RelatedA1,          //
  RelatedA2,          //
  RelatedA3,          //
  RelatedB0,          //
  RelatedB1,          //
  RelatedB2,          //
  // 0x18
  RelatedB3,       //
  MultiPacketEx,   // same as MultiPacket, but with the ability to send extended length packets
  Unknown26,       //
  Unknown27,       //
  Unknown28,       //
  ConnectionClose, //
  Unknown30        // Probably a more lightweight variant of ClientStart, containing only the client nonce
  = Value

  private def noDecoder(opcode: ControlPacketOpcode.Type) =
    (bits: BitVector) => Attempt.failure(Err(s"Could not find a marshaller for control packet $opcode (${bits.toHex})"))

  def getPacketDecoder(
      opcode: ControlPacketOpcode.Type
  ): (BitVector) => Attempt[DecodeResult[PlanetSideControlPacket]] =
    (opcode.id: @switch) match {
      // OPCODES 0x00-0f
      case 0x00 => control.HandleGamePacket.decode
      case 0x01 => control.ClientStart.decode
      case 0x02 => control.ServerStart.decode
      case 0x03 => control.MultiPacket.decode
      case 0x04 => noDecoder(Unknown4)
      case 0x05 => control.TeardownConnection.decode
      case 0x06 => noDecoder(Unknown6)
      case 0x07 => control.ControlSync.decode
      // 0x08
      case 0x08 => control.ControlSyncResp.decode
      case 0x09 => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket0)
      case 0x0a => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket1)
      case 0x0b => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket2)
      case 0x0c => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket3)
      case 0x0d => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket4)
      case 0x0e => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket5)
      case 0x0f => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket6)

      // OPCODES 0x10-1e
      case 0x10 => SlottedMetaPacket.decodeWithOpcode(SlottedMetaPacket7)
      case 0x11 => RelatedA.decodeWithOpcode(RelatedA0)
      case 0x12 => RelatedA.decodeWithOpcode(RelatedA1)
      case 0x13 => RelatedA.decodeWithOpcode(RelatedA2)
      case 0x14 => RelatedA.decodeWithOpcode(RelatedA3)
      case 0x15 => RelatedB.decodeWithOpcode(RelatedB0)
      case 0x16 => RelatedB.decodeWithOpcode(RelatedB1)
      case 0x17 => RelatedB.decodeWithOpcode(RelatedB2)
      // 0x18
      case 0x18 => RelatedB.decodeWithOpcode(RelatedB3)
      case 0x19 => control.MultiPacketEx.decode
      case 0x1a => noDecoder(Unknown26)
      case 0x1b => noDecoder(Unknown27)
      case 0x1c => noDecoder(Unknown28)
      case 0x1d => control.ConnectionClose.decode
      case 0x1e => control.Unknown30.decode
      case _    => noDecoder(opcode)
    }

  implicit val codec: Codec[this.Value] = PacketHelpers.createEnumerationCodec(this, uint8L)
}
