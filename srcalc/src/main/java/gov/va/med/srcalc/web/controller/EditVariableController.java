package gov.va.med.srcalc.web.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import gov.va.med.srcalc.domain.model.Variable;
import gov.va.med.srcalc.service.*;
import gov.va.med.srcalc.web.view.Views;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Web MVC controller for administration of variables.
 */
@Controller
@RequestMapping("/admin/variables/{variableKey}")
public class EditVariableController
{
    private final AdminService fAdminService;
    
    @Inject
    public EditVariableController(final AdminService adminService)
    {
        fAdminService = adminService;
    }
    
    @InitBinder
    protected void initBinder(final WebDataBinder binder)
    {
        binder.addValidators(new EditVariableValidator());
    }
    
    /**
     * Creates an {@link EditVariable} instance for the variable to edit.
     * @param variableKey the name of the variable to edit
     * @return the EditVariable instance
     * @throws InvalidIdentifierException
     */
    @ModelAttribute("variable")
    public EditVariable createEditVariable(
            @PathVariable final String variableKey)
            throws InvalidIdentifierException
    {
        return EditVariable.fromVariable(fAdminService.getVariable(variableKey));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView editVariable()
            throws InvalidIdentifierException
    {
        final ModelAndView mav = new ModelAndView(Views.EDIT_BOOLEAN_VARIABLE);
        // Note: "variable" is in the model via createEditVariable() above.
        
        // Add reference data (max lengths, valid values, etc.)
        mav.addObject("DISPLAY_NAME_MAX", Variable.DISPLAY_NAME_MAX);
        
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView saveVariable(
            @ModelAttribute("variable") @Valid final EditVariable editVariable,
            final BindingResult bindingResult)
                    throws InvalidIdentifierException
    {
        if (bindingResult.hasErrors())
        {
            // Re-show the edit screen.
            return editVariable();
        }

        fAdminService.updateVariable(editVariable);
        // Using the POST-redirect-GET pattern.
        return new ModelAndView("redirect:/admin/models");
    }
    
}
