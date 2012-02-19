package controllers;

import play.*;
import play.mvc.*;
import models.*;
import com.google.gson.*;
import java.lang.reflect.Type;

public class JsonApi extends Controller {
  public static void getTune(long id) {
    Tune tune = Tune.findById(id);
    renderJSON(tune,
	       new JsonSerializer<Tune>() {
		 public JsonElement serialize(Tune tune,
					      Type type,
					      JsonSerializationContext context) {
		   JsonObject obj = new JsonObject();
		   obj.addProperty("title", tune.title);
		   obj.add("staves", context.serialize(tune.staves));
		   return obj;
		 }
	       },
	       new JsonSerializer<Staff>() {
		 public JsonElement serialize(Staff staff,
					      Type type,
					      JsonSerializationContext context) {
		   JsonObject obj = new JsonObject();
		   obj.addProperty("name", staff.name);
		   return obj;
		 }
	       });
  }
}
