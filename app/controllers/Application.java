package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.db.jpa.*;

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
    private JPAApi jpaApi;

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
                page(page, 10, sortBy, order, filter),
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
            findById(id)
        );
        return ok(
            editForm.render(id, computerForm, options())
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
            return badRequest(editForm.render(id, computerForm, options()));
        }
        Computer computer = computerForm.get();
        updateComputer(id, computer);
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
            createForm.render(computerForm, options())
        );
    }
    
    /**
     * Handle the 'new computer form' submission 
     */
    @Transactional
    public Result save() {
        Form<Computer> computerForm = formFactory.form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(createForm.render(computerForm, options()));
        }
        Computer computer = computerForm.get();
        saveComputer(computer);
        flash("success", "Computer " + computer.name + " has been created");
        return GO_HOME;
    }
    
    /**
     * Handle computer deletion
     */
    @Transactional
    public Result delete(Long id) {
        deleteComputer(findById(id));
        flash("success", "Computer has been deleted");
        return GO_HOME;
    }


    public Company findByCompanyId(Long id) {
        return jpaApi.em().find(Company.class, id);
    }

    public Map<String,String> options() {
        @SuppressWarnings("unchecked")
        List<Company> companies = JPA.em().createQuery("from Company order by name").getResultList();
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Company c: companies) {
            options.put(c.id.toString(), c.name);
        }
        return options;
    }


    /**
     * Find a company by id.
     */
    public Computer findById(Long id) {
        return jpaApi.em().find(Computer.class, id);
    }

    /**
     * Update this computer.
     */
    public void updateComputer(Long id, Computer computer) {
        if(computer.company.id == null) {
            computer.company = null;
        } else {
            computer.company = findByCompanyId(computer.company.id);
        }
        computer.id = id;
        jpaApi.em().merge(computer);
    }

    /**
     * Insert this new computer.
     */
    public void saveComputer(Computer computer) {
        if(computer.company.id == null) {
            computer.company = null;
        } else {
            computer.company = findByCompanyId(computer.company.id);
        }
        jpaApi.em().persist(computer);
    }

    /**
     * Delete this computer.
     */
    public void deleteComputer(Computer computer) {
        jpaApi.em().remove(computer);
    }

    /**
     * Return a page of computer
     *
     * @param page Page to display
     * @param pageSize Number of computers per page
     * @param sortBy Computer property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public Page page(int page, int pageSize, String sortBy, String order, String filter) {
        if(page < 1) page = 1;
        Long total = (Long)jpaApi.em()
                .createQuery("select count(c) from Computer c where lower(c.name) like ?")
                .setParameter(1, "%" + filter.toLowerCase() + "%")
                .getSingleResult();
        @SuppressWarnings("unchecked")
        List<Computer> data = jpaApi.em()
                .createQuery("from Computer c where lower(c.name) like ? order by c." + sortBy + " " + order)
                .setParameter(1, "%" + filter.toLowerCase() + "%")
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return new Page(data, total, page, pageSize);
    }

    /**
     * Used to represent a computers page.
     */
    public static class Page {

        private final int pageSize;
        private final long totalRowCount;
        private final int pageIndex;
        private final List<Computer> list;

        public Page(List<Computer> data, long total, int page, int pageSize) {
            this.list = data;
            this.totalRowCount = total;
            this.pageIndex = page;
            this.pageSize = pageSize;
        }

        public long getTotalRowCount() {
            return totalRowCount;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public List<Computer> getList() {
            return list;
        }

        public boolean hasPrev() {
            return pageIndex > 1;
        }

        public boolean hasNext() {
            return (totalRowCount/pageSize) >= pageIndex;
        }

        public String getDisplayXtoYofZ() {
            int start = ((pageIndex - 1) * pageSize + 1);
            int end = start + Math.min(pageSize, list.size()) - 1;
            return start + " to " + end + " of " + totalRowCount;
        }

    }

}
            
