package gov.va.med.srcalc.web.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import gov.va.med.srcalc.domain.variable.Variable;
import gov.va.med.srcalc.domain.workflow.CalculationWorkflow;
import gov.va.med.srcalc.domain.workflow.NewCalculation;
import gov.va.med.srcalc.domain.workflow.SelectedCalculation;
import gov.va.med.srcalc.service.CalculationService;
import gov.va.med.srcalc.service.InvalidIdentifierException;
import gov.va.med.srcalc.web.view.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for creating a new Calculation.
 */
@Controller
public class CalculationController
{
    private static String SESSION_CALCULATION = "srcalc_calculation";
    
    private static final Logger fLogger = LoggerFactory.getLogger(CalculationController.class);
    
    private final CalculationService fCalculationService;
    
    @Inject
    public CalculationController(final CalculationService calculationService)
    {
        fCalculationService = calculationService;
    }
    
    /**
     * Returns the current CalculationWorkflow object form the HttpSession.
     * @return never null
     * @throws IllegalStateException if there is no current calculation
     */
    protected CalculationWorkflow getWorkflowFromSession(final HttpSession session)
    {
        final CalculationWorkflow workflow =
                (CalculationWorkflow)session.getAttribute(SESSION_CALCULATION);
        if (workflow == null)
        {
            throw new IllegalStateException("No current calculation.");
        }
        return workflow;
    }

    @RequestMapping(value = "/newCalc", method = RequestMethod.GET)
    public String presentSelection(final HttpSession session, final Model model)
    {
        // A Calculation object must be created here to store the start time for
        // the "Time To Completion" report.
        
        // FIXME: fake
        final int FAKE_PATIENT_DFN = 1;

        // Start the calculation.
        final NewCalculation newCalc = fCalculationService.startNewCalculation(FAKE_PATIENT_DFN);

        // Store the calculation in the HTTP Session.
        session.setAttribute(SESSION_CALCULATION, newCalc);
        
        // Present the view.
        model.addAttribute("calculation", newCalc.getCalculation());
	model.addAttribute("specialties", newCalc.getPossibleSpecialties());
	return Tile.SELECT_SPECIALTY;
    }
    
    @RequestMapping(value = "/selectSpecialty", method = RequestMethod.POST)
    public String setSpecialty(
            final HttpSession session,
            @RequestParam("specialty") final String specialtyName)
                    throws InvalidIdentifierException
    {
        final CalculationWorkflow workflow = getWorkflowFromSession(session);
        final SelectedCalculation selCalc =
                fCalculationService.setSpecialty(workflow.getCalculation(), specialtyName);
        session.setAttribute(SESSION_CALCULATION, selCalc);

        // Using the POST-redirect-GET pattern.
        return "redirect:/enterVars";
    }
    
    @RequestMapping(value = "/enterVars", method = RequestMethod.GET)
    public String presentVariableEntry(
            final HttpSession session,
            final Model model)
    {
        // Get the CalculationWorkflow from the session.
        final CalculationWorkflow workflow = getWorkflowFromSession(session);

        // Present the view.
        model.addAttribute("calculation", workflow.getCalculation());
        return Tile.ENTER_VARIABLES;
    }
    
    @RequestMapping(value = "/enterVars", method = RequestMethod.POST)
    public String enterVariables(
            final HttpSession session,
            final Model model,
            @ModelAttribute("variableEntry") final VariableEntry values,
            final BindingResult valuesBindingResult)
    {
        // Get the CalculationWorkflow from the session.
        final CalculationWorkflow workflow = getWorkflowFromSession(session);
        
        // Extract the values from the HTTP POST.
        // TODO dynamically
        final InputParserVisitor parserVisitor = new InputParserVisitor(values, valuesBindingResult);
        for (final Variable variable : workflow.getCalculation().getVariables())
        {
            try
            {
                variable.accept(parserVisitor);
            }
            catch (final Exception e)
            {
                // We know InputParserVisitor does not throw any Exceptions,
                // so just call it a RuntimeException.
                throw new RuntimeException("InputParserVisitor threw an Exception!", e);
            }
        }
        
        // If we have any binding errors, return to the Enter Variables screen.
        if (valuesBindingResult.hasErrors())
        {
            fLogger.debug(
                    "Invalid variables entered by user. Reshowing variable entry screen. Errors: {}",
                    valuesBindingResult.getAllErrors());
            return presentVariableEntry(session, model);
        }

        // Set the values on the Calculation.
        fCalculationService.runCalculation(
                workflow.getCalculation(), parserVisitor.getValues());

        // Using the POST-redirect-GET pattern.
        return "redirect:/displayResults";
    }
    
    @RequestMapping(value = "/displayResults", method = RequestMethod.GET)
    public String displayResults(
            final HttpSession session,
            final Model model)
    {
        // Get the CalculationWorkflow from the session.
        final CalculationWorkflow workflow = getWorkflowFromSession(session);
        
        model.addAttribute("calculation", workflow.getCalculation());
        return Tile.DISPLAY_RESULTS;
    }
}
