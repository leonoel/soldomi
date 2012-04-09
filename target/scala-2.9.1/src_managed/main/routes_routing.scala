// @SOURCE:/home/leonoel/atelier/notewar/conf/routes
// @HASH:c76b1c81e3fbcea38a1045c4bd23fdbda234bfd4
// @DATE:Sun Apr 01 13:34:41 CEST 2012

import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString

object Routes extends Router.Routes {


// @LINE:6
val controllers_Application_index0 = Route("GET", PathPattern(List(StaticPart("/"))))
                    

// @LINE:7
val controllers_Application_tunes1 = Route("GET", PathPattern(List(StaticPart("/tunes/"))))
                    

// @LINE:8
val controllers_Application_importNwc2 = Route("POST", PathPattern(List(StaticPart("/tunes/fromnwc/"))))
                    

// @LINE:9
val controllers_Application_showTune3 = Route("GET", PathPattern(List(StaticPart("/tunes/"),DynamicPart("id", """[^/]+"""),StaticPart("/"))))
                    

// @LINE:12
val controllers_JsonApi_tuneInfo4 = Route("GET", PathPattern(List(StaticPart("/jsonapi/tunes/"),DynamicPart("id", """[0-9]+"""))))
                    

// @LINE:13
val controllers_JsonApi_measureInfo5 = Route("GET", PathPattern(List(StaticPart("/jsonapi/tunes/"),DynamicPart("tuneId", """[0-9]+"""),StaticPart("/measures/"),DynamicPart("measurePosition", """[0-9]+"""))))
                    

// @LINE:16
val controllers_Assets_at6 = Route("GET", PathPattern(List(StaticPart("/assets/"),DynamicPart("file", """.+"""))))
                    
def documentation = List(("""GET""","""/""","""controllers.Application.index()"""),("""GET""","""/tunes/""","""controllers.Application.tunes()"""),("""POST""","""/tunes/fromnwc/""","""controllers.Application.importNwc()"""),("""GET""","""/tunes/$id<[^/]+>/""","""controllers.Application.showTune(id:Long)"""),("""GET""","""/jsonapi/tunes/$id<[0-9]+>""","""controllers.JsonApi.tuneInfo(id:Long)"""),("""GET""","""/jsonapi/tunes/$tuneId<[0-9]+>/measures/$measurePosition<[0-9]+>""","""controllers.JsonApi.measureInfo(tuneId:Long, measurePosition:java.lang.Integer)"""),("""GET""","""/assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)"""))
             
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:6
case controllers_Application_index0(params) => {
   call { 
        invokeHandler(_root_.controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Nil))
   }
}
                    

// @LINE:7
case controllers_Application_tunes1(params) => {
   call { 
        invokeHandler(_root_.controllers.Application.tunes(), HandlerDef(this, "controllers.Application", "tunes", Nil))
   }
}
                    

// @LINE:8
case controllers_Application_importNwc2(params) => {
   call { 
        invokeHandler(_root_.controllers.Application.importNwc(), HandlerDef(this, "controllers.Application", "importNwc", Nil))
   }
}
                    

// @LINE:9
case controllers_Application_showTune3(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(_root_.controllers.Application.showTune(id), HandlerDef(this, "controllers.Application", "showTune", Seq(classOf[Long])))
   }
}
                    

// @LINE:12
case controllers_JsonApi_tuneInfo4(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(_root_.controllers.JsonApi.tuneInfo(id), HandlerDef(this, "controllers.JsonApi", "tuneInfo", Seq(classOf[Long])))
   }
}
                    

// @LINE:13
case controllers_JsonApi_measureInfo5(params) => {
   call(params.fromPath[Long]("tuneId", None), params.fromPath[java.lang.Integer]("measurePosition", None)) { (tuneId, measurePosition) =>
        invokeHandler(_root_.controllers.JsonApi.measureInfo(tuneId, measurePosition), HandlerDef(this, "controllers.JsonApi", "measureInfo", Seq(classOf[Long], classOf[java.lang.Integer])))
   }
}
                    

// @LINE:16
case controllers_Assets_at6(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(_root_.controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String])))
   }
}
                    
}
    
}
                