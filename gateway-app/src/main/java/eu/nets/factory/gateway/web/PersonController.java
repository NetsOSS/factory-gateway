package eu.nets.factory.gateway.web;

import java.util.List;

import eu.nets.factory.gateway.model.Person;
import eu.nets.factory.gateway.model.PersonRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Transactional
public class PersonController {

    private final Logger log = getLogger(getClass());

    @Autowired
    private PersonRepository personRepository;

    @RequestMapping(method = GET, value = "/data/persons", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> list() {
        log.info("PersonController.list");
        return personRepository.findAll();
    }

    @RequestMapping(method = GET, value = "/data/search", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> search(@RequestParam(required = false) String name) {
        log.info("PersonController.search, name={}", name);

        List<Person> persons;

        if (name == null) {
            persons = personRepository.findAll();
        } else {
            persons = personRepository.findByNameLike("%" + name + "%");
        }

        return persons;
    }

    @RequestMapping(method = POST, value = "/data/persons", consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
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
