package controllers;

import models.Company;
import models.Person;
import play.mvc.*;
import play.data.FormFactory;
import play.Logger;
import play.db.jpa.*;

import views.html.*;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    Logger.ALogger logger = Logger.of(HomeController.class);

    @Inject
    private FormFactory formFactory;
    @Inject
    private JPAApi jpaApi;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    @Transactional(readOnly = true)
    public Result indexHome() {
        List<Person> lst = jpaApi.em().createQuery("select p from Person p", Person.class).getResultList();
        return ok(personlist.render(lst));
    }

    @Transactional
    public Result addPerson() {
        Person person = formFactory.form(Person.class).bindFromRequest().get();
        logger.debug("person : {}", person);
        jpaApi.em().persist(person);
        return redirect(routes.HomeController.indexHome());
    }

    public Result newPerson() {
        return ok(index.render(formFactory.form(Person.class)));
    }

}
