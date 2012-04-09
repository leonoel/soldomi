// @SOURCE:/home/leonoel/atelier/notewar/conf/routes
// @HASH:c76b1c81e3fbcea38a1045c4bd23fdbda234bfd4
// @DATE:Sun Apr 01 13:34:41 CEST 2012

import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString


// @LINE:16
// @LINE:13
// @LINE:12
// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
package controllers {

// @LINE:13
// @LINE:12
class ReverseJsonApi {
    


 
// @LINE:13
def measureInfo(tuneId:Long, measurePosition:java.lang.Integer) = {
   Call("GET", "/jsonapi/tunes/" + implicitly[PathBindable[Long]].unbind("tuneId", tuneId) + "/measures/" + implicitly[PathBindable[java.lang.Integer]].unbind("measurePosition", measurePosition))
}
                                                        
 
// @LINE:12
def tuneInfo(id:Long) = {
   Call("GET", "/jsonapi/tunes/" + implicitly[PathBindable[Long]].unbind("id", id))
}
                                                        

                      
    
}
                            

// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
class ReverseApplication {
    


 
// @LINE:7
def tunes() = {
   Call("GET", "/tunes/")
}
                                                        
 
// @LINE:8
def importNwc() = {
   Call("POST", "/tunes/fromnwc/")
}
                                                        
 
// @LINE:9
def showTune(id:Long) = {
   Call("GET", "/tunes/" + implicitly[PathBindable[Long]].unbind("id", id) + "/")
}
                                                        
 
// @LINE:6
def index() = {
   Call("GET", "/")
}
                                                        

                      
    
}
                            

// @LINE:16
class ReverseAssets {
    


 
// @LINE:16
def at(file:String) = {
   Call("GET", "/assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                        

                      
    
}
                            
}
                    


// @LINE:16
// @LINE:13
// @LINE:12
// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
package controllers.javascript {

// @LINE:13
// @LINE:12
class ReverseJsonApi {
    


 
// @LINE:13
def measureInfo = JavascriptReverseRoute(
   "controllers.JsonApi.measureInfo",
   """
      function(tuneId,measurePosition) {
      return _wA({method:"GET", url:"/jsonapi/tunes/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("tuneId", tuneId) + "/measures/" + (""" + implicitly[PathBindable[java.lang.Integer]].javascriptUnbind + """)("measurePosition", measurePosition)})
      }
   """
)
                                                        
 
// @LINE:12
def tuneInfo = JavascriptReverseRoute(
   "controllers.JsonApi.tuneInfo",
   """
      function(id) {
      return _wA({method:"GET", url:"/jsonapi/tunes/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id)})
      }
   """
)
                                                        

                      
    
}
                            

// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
class ReverseApplication {
    


 
// @LINE:7
def tunes = JavascriptReverseRoute(
   "controllers.Application.tunes",
   """
      function() {
      return _wA({method:"GET", url:"/tunes/"})
      }
   """
)
                                                        
 
// @LINE:8
def importNwc = JavascriptReverseRoute(
   "controllers.Application.importNwc",
   """
      function() {
      return _wA({method:"POST", url:"/tunes/fromnwc/"})
      }
   """
)
                                                        
 
// @LINE:9
def showTune = JavascriptReverseRoute(
   "controllers.Application.showTune",
   """
      function(id) {
      return _wA({method:"GET", url:"/tunes/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/"})
      }
   """
)
                                                        
 
// @LINE:6
def index = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"/"})
      }
   """
)
                                                        

                      
    
}
                            

// @LINE:16
class ReverseAssets {
    


 
// @LINE:16
def at = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"/assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                                                        

                      
    
}
                            
}
                    


// @LINE:16
// @LINE:13
// @LINE:12
// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
package controllers.ref {

// @LINE:13
// @LINE:12
class ReverseJsonApi {
    


 
// @LINE:13
def measureInfo(tuneId:Long, measurePosition:java.lang.Integer) = new play.api.mvc.HandlerRef(
   controllers.JsonApi.measureInfo(tuneId, measurePosition), HandlerDef(this, "controllers.JsonApi", "measureInfo", Seq(classOf[Long], classOf[java.lang.Integer]))
)
                              
 
// @LINE:12
def tuneInfo(id:Long) = new play.api.mvc.HandlerRef(
   controllers.JsonApi.tuneInfo(id), HandlerDef(this, "controllers.JsonApi", "tuneInfo", Seq(classOf[Long]))
)
                              

                      
    
}
                            

// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
class ReverseApplication {
    


 
// @LINE:7
def tunes() = new play.api.mvc.HandlerRef(
   controllers.Application.tunes(), HandlerDef(this, "controllers.Application", "tunes", Seq())
)
                              
 
// @LINE:8
def importNwc() = new play.api.mvc.HandlerRef(
   controllers.Application.importNwc(), HandlerDef(this, "controllers.Application", "importNwc", Seq())
)
                              
 
// @LINE:9
def showTune(id:Long) = new play.api.mvc.HandlerRef(
   controllers.Application.showTune(id), HandlerDef(this, "controllers.Application", "showTune", Seq(classOf[Long]))
)
                              
 
// @LINE:6
def index() = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq())
)
                              

                      
    
}
                            

// @LINE:16
class ReverseAssets {
    


 
// @LINE:16
def at(path:String, file:String) = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]))
)
                              

                      
    
}
                            
}
                    
                