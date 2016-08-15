package models;

import java.util.*;
import javax.persistence.*;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.*;

/**
 * Created by apple on 2016. 8. 14..
 */
@Entity
@Table(name="tb_person")
public class Person extends Model {
    @Id
    @Column(name="person_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name="person_name")
    @Constraints.Required
    public String name;

    @Column(name="person_nick")
    public String nick;

    @Column(name="complete_flag")
    public boolean done;

    @Column(name="reg_date")
    @Formats.DateTime(pattern="yyyy/MM/dd")
    public Date dueDate = new Date();

    public static Finder<Long, Person> find = new Finder<Long,Person>(Person.class);
}
