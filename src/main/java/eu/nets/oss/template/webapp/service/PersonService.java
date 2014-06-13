package eu.nets.oss.template.webapp.service;

import eu.nets.oss.template.webapp.model.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

}
