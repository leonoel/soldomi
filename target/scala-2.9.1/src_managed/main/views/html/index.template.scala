
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.i18n._
import play.api.templates.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import com.avaje.ebean._
import play.mvc.Http.Context.Implicit._
import views.html._
/**/
object index extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[List[Tune],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(tunes: List[Tune]):play.api.templates.Html = {
        _display_ {import helper._


Seq(format.raw/*1.21*/("""
"""),format.raw/*3.1*/("""
"""),_display_(Seq(/*4.2*/main("NoteWar")/*4.17*/ {_display_(Seq(format.raw/*4.19*/("""

"""),_display_(Seq(/*6.2*/if(flash.get("error"))/*6.24*/ {_display_(Seq(format.raw/*6.26*/("""
<p class="error">
  """),_display_(Seq(/*8.4*/flash/*8.9*/.get("error"))),format.raw/*8.22*/("""
</p>
""")))})),format.raw/*10.2*/("""

    
<h2>"""),_display_(Seq(/*13.6*/tunes/*13.11*/.size())),format.raw/*13.18*/(""" tune(s)</h2>
  """),_display_(Seq(/*14.4*/if(!tunes.isEmpty())/*14.24*/ {_display_(Seq(format.raw/*14.26*/("""
    <div class="tunes">
      <ul>
	"""),_display_(Seq(/*17.3*/for(tune <- tunes) yield /*17.21*/ {_display_(Seq(format.raw/*17.23*/("""
		     <li>
		       <a href=""""),_display_(Seq(/*19.20*/routes/*19.26*/.Application.showTune(tune.id))),format.raw/*19.56*/("""">"""),_display_(Seq(/*19.59*/tune/*19.63*/.title)),format.raw/*19.69*/("""</a>
		     </li>
	""")))})),format.raw/*21.3*/("""
      </ul>
    </div>
  """)))})),format.raw/*24.4*/("""
  <div class="addNwc">
    <h2>Import nwc file :</h2>
    """),_display_(Seq(/*27.6*/form(action = routes.Application.importNwc, 'enctype -> "multipart/form-data")/*27.84*/ {_display_(Seq(format.raw/*27.86*/("""
      <input type="file" name="nwc"/>
      <input type="submit" value="Upload" />
    """)))})),format.raw/*30.6*/("""

  </div>
""")))})),format.raw/*33.2*/("""
"""))}
    }
    
    def render(tunes:List[Tune]) = apply(tunes)
    
    def f:((List[Tune]) => play.api.templates.Html) = (tunes) => apply(tunes)
    
    def ref = this

}
                /*
                    -- GENERATED --
                    DATE: Sun Apr 01 13:34:42 CEST 2012
                    SOURCE: /home/leonoel/atelier/notewar/app/views/index.scala.html
                    HASH: b095c4de5fe16fce7e41488ea981a34b7041ee53
                    MATRIX: 759->1|866->20|893->38|924->40|947->55|981->57|1013->60|1043->82|1077->84|1128->106|1140->111|1174->124|1212->131|1254->143|1268->148|1297->155|1344->172|1373->192|1408->194|1476->232|1510->250|1545->252|1608->284|1623->290|1675->320|1709->323|1722->327|1750->333|1801->353|1859->380|1949->440|2036->518|2071->520|2191->609|2234->621
                    LINES: 27->1|31->1|32->3|33->4|33->4|33->4|35->6|35->6|35->6|37->8|37->8|37->8|39->10|42->13|42->13|42->13|43->14|43->14|43->14|46->17|46->17|46->17|48->19|48->19|48->19|48->19|48->19|48->19|50->21|53->24|56->27|56->27|56->27|59->30|62->33
                    -- GENERATED --
                */
            