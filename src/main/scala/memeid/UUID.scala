package memeid

import java.lang.Long.compareUnsigned
import java.util.{UUID => JUUID}

import scala.reflect.ClassTag

/**
 * A class that represents an immutable universally unique identifier (UUID).
 * A UUID represents a 128-bit value.
 *
 * @see [[https://tools.ietf.org/html/rfc4122]]
 */
sealed trait UUID extends Comparable[UUID] {

  private[memeid] val juuid: JUUID

  /** The most significant 64 bits of this UUID's 128 bit value */
  @inline def msb: Long = juuid.getMostSignificantBits

  /** The least significant 64 bits of this UUID's 128 bit value */
  @inline def lsb: Long = juuid.getLeastSignificantBits

  /**
   * Returns this `UUID` as the provided type if versions match;
   * otherwise, returns `None`.
   */
  def as[A <: UUID: ClassTag]: Option[A] = this match {
    case a: A => Some(a)
    case _    => None
  }

  /**
   * Returns `true` if this UUID matches the provided type;
   * otherwise, returns `false`.
   */
  def is[A <: UUID: ClassTag]: Boolean = this match {
    case _: A => true
    case _    => false
  }

  /**
   * The variant field determines the layout of the [[UUID]].
   *
   * The variant field consists of a variable number of
   * the most significant bits of octet 8 of the [[UUID]].
   *
   * The variant number has the following meaning:
   *
   * - '''0''': Reserved for NCS backward compatibility
   * - '''2''': [[https://tools.ietf.org/html/rfc4122 IETF RFC 4122]]
   * - '''6''': Reserved, Microsoft Corporation backward compatibility
   * - '''7''': Reserved for future definition
   *
   * Interoperability, in any form, with variants other than the one
   * defined here is not guaranteed, and is not likely to be an issue in
   * practice.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.1]]
   */
  @inline def variant: Int = juuid.variant

  @inline def version: Int = juuid.version

  @SuppressWarnings(
    Array("scalafix:Disable.equals", "scalafix:Disable.Any", "scalafix:DisableSyntax.==")
  )
  override def equals(obj: Any): Boolean = obj match {
    case x: UUID => compareTo(x) == 0
    case _       => false
  }

  override def compareTo(x: UUID): Int = {
    compareUnsigned(msb, x.msb) match {
      case 0     => compareUnsigned(lsb, x.lsb)
      case other => other
    }
  }

  @SuppressWarnings(Array("scalafix:Disable.hashCode"))
  override def hashCode(): Int = juuid.hashCode

  @SuppressWarnings(Array("scalafix:Disable.toString"))
  override def toString: String = juuid.toString

}

object UUID extends Constructors {

  /**
   * The nil UUID is special form of UUID that is specified to have all 128 bits set to zero.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.7]]
   */
  case object Nil extends UUID { override val juuid: JUUID = new JUUID(0, 0) }

  /**
   * Version 1 UUIDs are those generated using a timestamp and the MAC address of the
   * computer on which it was generated.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.3]]
   */
  final class V1 private[memeid] (override private[memeid] val juuid: JUUID) extends UUID

  /**
   * DCE Security version, with embedded POSIX UIDs.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.3]]
   */
  final class V2 private[memeid] (override private[memeid] val juuid: JUUID) extends UUID

  /**
   * Version 3 UUIDs are those generated by hashing a namespace identifier and name using
   * MD5 as the hashing algorithm.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.3]]
   */
  final class V3 private[memeid] (override private[memeid] val juuid: JUUID) extends UUID

  /**
   * Version 4 UUIDs are those generated using random numbers.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.3]]
   */
  final class V4 private[memeid] (override private[memeid] val juuid: JUUID) extends UUID

  /**
   * Version 5 UUIDs are those generated by hashing a namespace identifier and name using
   * SHA-1 as the hashing algorithm.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.3]]
   */
  final class V5 private[memeid] (override private[memeid] val juuid: JUUID) extends UUID

  /**
   * Not standard-version UUIDs. Includes the extracted version from the most significant bits.
   *
   * @see [[https://tools.ietf.org/html/rfc4122#section-4.1.3]]
   */
  final class UnknownVersion private[memeid] (
      override private[memeid] val juuid: JUUID
  ) extends UUID
}
