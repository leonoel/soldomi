
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
object showTune extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[Tune,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(tune: Tune):play.api.templates.Html = {
        _display_ {

Seq(format.raw/*1.14*/("""

"""),_display_(Seq(/*3.2*/main("NoteWar - " + tune.title)/*3.33*/ {_display_(Seq(format.raw/*3.35*/("""

<script type="text/javascript" src=""""),_display_(Seq(/*5.38*/routes/*5.44*/.Assets.at("javascripts/jquery-1.7.1.js"))),format.raw/*5.85*/(""""></script>
<script type="text/javascript" src=""""),_display_(Seq(/*6.38*/routes/*6.44*/.Assets.at("javascripts/vexflow-free.js"))),format.raw/*6.85*/(""""></script>
<script type="text/javascript" src=""""),_display_(Seq(/*7.38*/routes/*7.44*/.Assets.at("javascripts/notewar.js"))),format.raw/*7.80*/(""""></script>

<script type="text/javascript">
$(document).ready(function() """),format.raw("""{"""),format.raw/*10.31*/("""
  NoteWar.init($("#tuneRenderer").get(0), '/jsonapi/tunes/"""),_display_(Seq(/*11.60*/(tune.id))),format.raw/*11.69*/("""');
"""),format.raw("""}"""),format.raw/*12.2*/(""");
</script>


<h3>"""),_display_(Seq(/*16.6*/(tune.title))),format.raw/*16.18*/("""</h3>

<canvas id="tuneRenderer" width=1000 height=600></canvas>

""")))})),format.raw/*20.2*/("""
"""))}
    }
    
    def render(tune:Tune) = apply(tune)
    
    def f:((Tune) => play.api.templates.Html) = (tune) => apply(tune)
    
    def ref = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Apr 09 11:53:37 CEST 2012
                    SOURCE: /home/leonoel/atelier/notewar/app/views/showTune.scala.html
                    HASH: 73da4ceb0f510060f5484b6639b32b41f2775487
                    MATRIX: 756->1|840->13|872->16|911->47|945->49|1014->88|1028->94|1090->135|1169->184|1183->190|1245->231|1324->280|1338->286|1395->322|1517->397|1608->457|1639->466|1690->471|1740->491|1774->503|1872->570
                    LINES: 27->1|30->1|32->3|32->3|32->3|34->5|34->5|34->5|35->6|35->6|35->6|36->7|36->7|36->7|39->10|40->11|40->11|41->12|45->16|45->16|49->20
                    -- GENERATED --
                */
            