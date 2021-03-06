package gov.va.med.srcalc.web.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import gov.va.med.srcalc.domain.calculation.Calculation;
import gov.va.med.srcalc.service.*;
import gov.va.med.srcalc.web.view.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for creating a new Calculation.
 */
@Controller
public class CalculationController
{
    private final CalculationService fCalculationService;
    
    /**
     * Constructs an instance.
     * @param calculationService the service to use for various operations when
     *          performing a calculation
     */
    @Inject
    public CalculationController(final CalculationService calculationService)
    {
        fCalculationService = calculationService;
    }
    
    /**
     * Creates a brand new calculation with the specified dfn. Any existing calculation is
     * lost.
     * @param patientDfn the desired patient's dfn
     * @param session the current session
     */
    @RequestMapping(value = "/newCalc", method = RequestMethod.GET, params="force")
    public ModelAndView forceStartNewCalculation(
            @RequestParam(value = "patientDfn") final int patientDfn,
            final HttpSession session)
    {
        // Start the calculation. A Calculation object must be created here to
        // store the start time for reporting.
        final Calculation calc = fCalculationService.startNewCalculation(patientDfn);
        // Store the calculation in the HTTP Session.
        SrcalcSession.setCalculationSession(session, new CalculationSession(calc));
        
        // Present the view.
        final ModelAndView mav = new ModelAndView(Views.SELECT_SPECIALTY);
        mav.addObject("calculation", calc);
        // Also add the valid specialties for user selection.
        mav.addObject("specialties", fCalculationService.getValidSpecialties());
        return mav;
    }

    /**
     * Starts a new calculation if there was no previous calculation in the session.
     * If there was a previous calculation, redirects in order to ask the user if they want
     * to start a completely new calculation with the new patient DFN.
     * @param patientDfn the new patient's dfn
     * @param session the current session
     */
    @RequestMapping(value = "/newCalc", method = RequestMethod.GET)
    public ModelAndView startNewCalculation(
            @RequestParam(value = "patientDfn") final int patientDfn,
            final HttpSession session)
    {
        // If there is a calculation already in the session, ask the user
        // if they wish to override the in-progress calculation.
        if(SrcalcSession.hasCalculationSession(session))
        {
            final CalculationSession calcSession = SrcalcSession.getCalculationSession(session);
            final Calculation calc = calcSession.getCalculation();
            final ModelAndView mav = new ModelAndView(Views.CONFIRM_NEW_CALC);
            mav.addObject("calculation", calc);
            mav.addObject("newPatientDfn", patientDfn);
            return mav;
        }
        
        // If there is no calculation in the session, we start a new calculation.
        return forceStartNewCalculation(patientDfn, session);
    }
    
    /**
     * Sets the selected specialty for the current calculation.
     * @param session the current session
     * @param specialtyName the selected specialty name
     * @throws InvalidIdentifierException if the specialty name is invalid
     */
    @RequestMapping(value = "/selectSpecialty", method = RequestMethod.POST)
    public String setSpecialty(
            final HttpSession session,
            @RequestParam("specialty") final String specialtyName)
                    throws InvalidIdentifierException
    {
        final CalculationSession cs = SrcalcSession.getCalculationSession(session);
        fCalculationService.setSpecialty(cs.getCalculation(), specialtyName);

        // Using the POST-redirect-GET pattern.
        return "redirect:/enterVars";
    }
    
    // Variable entry is in EnterVariablesController.
    
}
