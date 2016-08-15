package controllers;

import models.Person;
import play.mvc.*;
import play.data.FormFactory;
import play.Logger;

import views.html.*;

import javax.inject.Inject;
import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    Logger.ALogger logger = Logger.of(HomeController.class);

    @Inject
    private FormFactory formFactory;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        List<Person> lst = Person.find.where().orderBy("dueDate desc").findList();
        return ok(list.render(lst));
    }

    public Result addPerson() {
        Person person = formFactory.form(Person.class).bindFromRequest().get();
        logger.debug("person : {}", person);
        person.save();
        return redirect(routes.HomeController.index());
    }

    public Result newPerson() {
        return ok(index.render(formFactory.form(Person.class)));
    }

}
