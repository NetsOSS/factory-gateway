package eu.nets.oss.template.webapp.web;

import java.util.List;

import eu.nets.oss.template.webapp.model.Person;
import eu.nets.oss.template.webapp.model.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.util.MimeTypeUtils.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Transactional
public class PersonController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PersonRepository personRepository;

    @RequestMapping(method = GET, value = "/persons", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> list() {
        log.info("PersonController.list");
        return personRepository.findAll();
    }

    @RequestMapping(method = POST, value = "/persons", consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonModel create(@RequestParam String name) {
        log.info("PersonController.create");
        Person person = new Person(name);
        personRepository.save(person);
        return new PersonModel(person.getId(), person.getName());
    }

    public static class PersonModel {
        public Long id;

        public String name;

        public PersonModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
