// Thank help from Jiuyang Liu to write this code.
import mill._
import mill.modules.Util
import scalalib._
import mill.contrib.buildinfo.BuildInfo
import $file.chisel3.build
import $file.firrtl.build
import $file.treadle.build
import mill.define.Target


object myfirrtl extends firrtl.build.firrtlCrossModule("2.12.11") {
  override def millSourcePath = super.millSourcePath / 'firrtl
}
object mychisel3 extends chisel3.build.chisel3CrossModule("2.12.11") {
  override def millSourcePath = super.millSourcePath / 'chisel3
  def firrtlModule: Option[PublishModule] = Some(myfirrtl)
  def treadleModule: Option[PublishModule] = Some(mytreadle)
}

object mytreadle extends treadle.build.treadleCrossModule("2.12.11") {
  override def millSourcePath = super.millSourcePath / 'treadle
  def firrtlModule: Option[PublishModule] = Some(myfirrtl)
}

trait CommonModule extends ScalaModule {
  def scalaVersion = "2.12.10"

  override def scalacOptions = Seq("-Xsource:2.11")

  override def moduleDeps: Seq[ScalaModule] = Seq(mychisel3)

  private val macroParadise = ivy"org.scalamacros:::paradise:2.1.0"

  override def compileIvyDeps = Agg(macroParadise)

  override def scalacPluginIvyDeps = Agg(macroParadise)
}

object config extends CommonModule {
  override def millSourcePath = super.millSourcePath / 'design / 'craft
}

object hardfloat extends CommonModule with SbtModule
object rocketchip extends CommonModule with SbtModule {
  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"${scalaOrganization()}:scala-reflect:${scalaVersion()}",
    ivy"org.json4s::json4s-jackson:3.6.1"
  )

  object macros extends CommonModule with SbtModule

  override def moduleDeps = super.moduleDeps ++ Seq(config, macros, hardfloat)

  override def mainClass = Some("rocketchip.Generator")
}
object inclusivecache extends CommonModule {

  override def moduleDeps = super.moduleDeps ++ Seq(rocketchip)

  override def millSourcePath = super.millSourcePath / 'design / 'craft / 'inclusivecache

}
object sifiveblocks extends CommonModule {

  override def moduleDeps = super.moduleDeps ++ Seq(rocketchip)

  override def millSourcePath = super.millSourcePath

}
object boom extends CommonModule {

  override def moduleDeps = super.moduleDeps ++ Seq(rocketchip, inclusivecache)

  override def millSourcePath = super.millSourcePath

}

object chiselexamples extends CommonModule {
  override def moduleDeps = super.moduleDeps ++ Seq(rocketchip)

  override def millSourcePath = super.millSourcePath
}

object vlsu extends CommonModule {
  override def moduleDeps = super.moduleDeps ++ Seq(rocketchip, boom)

  override def millSourcePath = super.millSourcePath
}

object stone extends CommonModule {
  override def moduleDeps = super.moduleDeps ++ Seq(rocketchip, boom)

  override def millSourcePath = super.millSourcePath
}

object integration extends CommonModule {
  override def moduleDeps = super.moduleDeps ++
    Seq(
      boom,
      vlsu,
      inclusivecache,
      rocketchip,
      mychisel3,
      sifiveblocks,
      chiselexamples,
      stone,
    )

  override def forkArgs: Target[Seq[String]] = Seq("-Xmx128G")
  override def ivyDeps = Agg(
    ivy"com.lihaoyi::upickle:latest.integration",
    ivy"com.lihaoyi::os-lib:latest.integration",
    ivy"com.lihaoyi::pprint:latest.integration"
  )
}