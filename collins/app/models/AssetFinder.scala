package models

import conversions._

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.{BinaryOperatorNodeLogicalBoolean, LogicalBoolean}

import java.util.Date

case class AssetFinder(
  tag: Option[String],
  status: Option[Status.Enum],
  assetType: Option[AssetType.Enum],
  createdAfter: Option[Date],
  createdBefore: Option[Date],
  updatedAfter: Option[Date],
  updatedBefore: Option[Date])
{
  def asLogicalBoolean(a: Asset): LogicalBoolean = {
    val tagBool = tag.map((a.tag === _))
    val statusBool = status.map((a.status === _.id))
    val typeBool = assetType.map((a.asset_type === _.id))
    val createdAfterTs = createdAfter.map((a.created gte _.asTimestamp))
    val createdBeforeTs = createdBefore.map((a.created lte _.asTimestamp))
    val updatedAfterTs = Some((a.updated gte updatedAfter.map(_.asTimestamp).?))
    val updatedBeforeTs = Some((a.updated lte updatedBefore.map(_.asTimestamp).?))
    val ops = Seq(tagBool, statusBool, typeBool, createdAfterTs, createdBeforeTs, updatedAfterTs,
      updatedBeforeTs).filter(_ != None).map(_.get)
    ops.reduceRight((a,b) => new BinaryOperatorNodeLogicalBoolean(a, b, "and"))
  }
}