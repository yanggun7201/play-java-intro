package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;

/**
 * Company entity managed by JPA
 */
@Entity 
public class Company {

    @Id
    public Long id;
    
    @Constraints.Required
    public String name;
}

