package gov.va.med.srcalc.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import gov.va.med.srcalc.db.SpecialtyDao;
import gov.va.med.srcalc.domain.Specialty;

/**
 * Default implementation of {@link SpecialtyService}.
 */
public class DefaultSpecialtyService implements SpecialtyService
{
    private final SpecialtyDao fSpecialtyDao;
    
    @Inject // Allow arguments to be autowired.
    public DefaultSpecialtyService(SpecialtyDao specialtyDao)
    {
        fSpecialtyDao = specialtyDao;
    }
    
    @Transactional
    public List<Specialty> getAllSpecialties()
    {
        return fSpecialtyDao.getAllSpecialties();
    }
}
