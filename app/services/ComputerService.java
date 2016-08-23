package services;

import controllers.Page;
import models.Company;
import models.Computer;

import play.db.jpa.*;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2016. 8. 23..
 */
public class ComputerService {

    @Inject
    private JPAApi jpaApi;

    /**
     * Find a company by id.
     */
    public Computer findById(Long id) {
        return jpaApi.em().find(Computer.class, id);
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
}
