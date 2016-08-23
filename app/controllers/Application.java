package controllers;

import play.mvc.*;
import play.data.*;
import play.db.jpa.*;

import services.ComputerService;
import views.html.*;

import models.*;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage a database of computers
 */
public class Application extends Controller {

    @Inject
    private FormFactory formFactory;
    @Inject
    private ComputerService computerService;

    /**
     * This result directly redirect to application home.
     */
    public Result GO_HOME = redirect(
        routes.Application.list(0, "name", "asc", "")
    );
    
    /**
     * Handle default path requests, redirect to computers list
     */
    public Result index() {
        return GO_HOME;
    }

    /**
     * Display the paginated list of computers.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on computer names
     */
    @Transactional(readOnly=true)
    public Result list(int page, String sortBy, String order, String filter) {
        return ok(
            list.render(
                computerService.page(page, 10, sortBy, order, filter),
                sortBy, order, filter
            )
        );
    }
    
    /**
     * Display the 'edit form' of a existing Computer.
     *
     * @param id Id of the computer to edit
     */
    @Transactional(readOnly=true)
    public Result edit(Long id) {
        Form<Computer> computerForm = formFactory.form(Computer.class).fill(
            computerService.findById(id)
        );
        return ok(
            editForm.render(id, computerForm, computerService.options())
        );
    }
    
    /**
     * Handle the 'edit form' submission 
     *
     * @param id Id of the computer to edit
     */
    @Transactional
    public Result update(Long id) {
        Form<Computer> computerForm = formFactory.form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(editForm.render(id, computerForm, computerService.options()));
        }
        Computer computer = computerForm.get();
        computerService.updateComputer(id, computer);
        flash("success", "Computer " + computer.name + " has been updated");
        return GO_HOME;
    }
    
    /**
     * Display the 'new computer form'.
     */
    @Transactional(readOnly=true)
    public Result create() {
        Form<Computer> computerForm = formFactory.form(Computer.class);
        return ok(
            createForm.render(computerForm, computerService.options())
        );
    }
    
    /**
     * Handle the 'new computer form' submission 
     */
    @Transactional
    public Result save() {
        Form<Computer> computerForm = formFactory.form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(createForm.render(computerForm, computerService.options()));
        }
        Computer computer = computerForm.get();
        computerService.saveComputer(computer);
        flash("success", "Computer " + computer.name + " has been created");
        return GO_HOME;
    }
    
    /**
     * Handle computer deletion
     */
    @Transactional
    public Result delete(Long id) {
        computerService.deleteComputer(computerService.findById(id));
        flash("success", "Computer has been deleted");
        return GO_HOME;
    }

}
            
