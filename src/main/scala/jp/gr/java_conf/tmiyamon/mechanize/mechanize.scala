package jp.gr.java_conf.tmiyamon.mechanize

import com.meterware.httpunit._
import com.meterware.httpunit.cookies._

import scala.util.control.Exception._
import scala.collection.JavaConversions._

object Mechanize {
    def main(args:Array[String]) = {
        val f = Mechanize().get("http://www.google.com/").forms.head
        f += ("q" ->  "hello")
        println(f.submit().xml \\ "INPUT")

    }
    
    def apply() = new Agent
}

class Agent {
    ClientProperties.getDefaultProperties.setAutoRedirect(true);
    HttpUnitOptions.setScriptingEnabled(false);
    CookieProperties.setPathMatchingStrict(true);
    CookieProperties.setDomainMatchingStrict(true);

    val wc = new WebConversation()
    wc.clearContents()

    def get(url:String) = new Page(wc.getResponse(new GetMethodWebRequest(url)))
}

class Page(resp: WebResponse) {
    def links = resp.getLinks.map(new Link(_))
    def forms = resp.getForms().map(new Form(_))

    def text = resp.getText()
    def title = resp.getTitle()
    def xml = {
        import scala.xml.parsing.NoBindingFactoryAdapter
        import scala.xml.TopScope
        import javax.xml.transform.TransformerFactory
        import javax.xml.transform.dom.DOMSource
        import javax.xml.transform.sax.SAXResult

        val saxHandler = new NoBindingFactoryAdapter()

        saxHandler.scopeStack.push(TopScope)
            TransformerFactory.newInstance.newTransformer()
                .transform(new DOMSource(resp.getDOM), new SAXResult(saxHandler))
        saxHandler.scopeStack.pop
        saxHandler.rootElem
    }
}

class Link(link:WebLink) {
    val text = link.getText
    val href = link.getURLString

    def click = new Page(link.click)
}

class Form(form: WebForm) extends scala.collection.mutable.Map[String, String] {
    def -=(key: String) = {
        form.removeParameter(key); 
        this
    }
    def +=(kv: (String, String)) = {
        form.setParameter(kv._1, kv._2); 
        this
    }
    def get(key: String) = {
        form.getParameterValue(key) match {
            case value:String if value == null || value.isEmpty => None
            case value:String => Some(value)
            case _ => None
        }
    }
    def iterator() = Iterator[(String, String)]()

    def action() = form.getAction

    def submit() = new Page(form.submit(form.getSubmitButtons().head))
}
    


// vim: set ts=4 sw=4 et:
