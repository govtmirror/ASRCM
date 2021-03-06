package gov.va.med.srcalc.web.controller.admin;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import gov.va.med.srcalc.domain.model.AbstractNumericalVariable;
import gov.va.med.srcalc.domain.model.DiscreteNumericalVariable;
import gov.va.med.srcalc.service.AdminService;
import gov.va.med.srcalc.test.util.IntegrationTest;
import gov.va.med.srcalc.test.util.TestHelpers;
import gov.va.med.srcalc.web.SrcalcUrls;
import gov.va.med.srcalc.web.view.Views;
import gov.va.med.srcalc.web.view.admin.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests the various NewVarControllers, e.g. {@link NewBooleanVarController} and
 * {@link NewMultiSelectVarController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration  // need to tell Spring to instantiate a WebApplicationContext.
@ContextConfiguration({"/srcalc-context.xml", "/srcalc-controller.xml", "/test-context.xml"})
@Transactional // run each test in its own (rolled-back) transaction
public class NewVarControllerIT extends IntegrationTest
{
    @Autowired
    WebApplicationContext fWac;
    
    @Autowired
    AdminService fAdminService;
    
    private MockMvc fMockMvc;
    
    @Before
    public void setup()
    {
        fMockMvc = MockMvcBuilders.webAppContextSetup(fWac).build();
    }

    @Test
    public final void testNewBooleanValid() throws Exception
    {
        final String key = "testNewVariableValidKey";

        fMockMvc.perform(get(NewBooleanVarController.BASE_URL))
            .andExpect(status().isOk())
            .andExpect(model().attribute(
                    NewVarController.ATTRIBUTE_VARIABLE, isA(EditBooleanVar.class)));
        
        fMockMvc.perform(post(NewBooleanVarController.BASE_URL)
                .param("key", key)
                .param("displayName", "myDisplayName")
                .param("helpText", "myHelpText")
                .param("groupId", "1"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl(SrcalcUrls.MODEL_ADMIN_HOME));
        
        // Verify that the variable was actually created. Individual properties
        // are tested in EditBooleanVarTest.
        assertEquals(key, fAdminService.getVariable(key).getKey());
    }
    
    @Test
    public final void testNewBooleanNoDisplayName() throws Exception
    {
        fMockMvc.perform(post(NewBooleanVarController.BASE_URL)
                .param("key", "newKey")
                .param("helpText", "myHelpText")
                .param("groupId", "1"))
            .andExpect(model().attributeHasErrors(NewVarController.ATTRIBUTE_VARIABLE));
    }
    
    @Test
    public final void testNewBooleanDuplicateKey() throws Exception
    {
        fMockMvc.perform(post(NewBooleanVarController.BASE_URL)
                .param("key", "preopPneumonia")
                .param("displayName", "myDisplayName")
                .param("helpText", "myHelpText")
                .param("groupId", "1"))
            .andExpect(model().attributeHasErrors(NewVarController.ATTRIBUTE_VARIABLE));
        
        // Normally the Session would be dead and gone by now, but in these ITs
        // the Transaction is still open, so manually clear the Session due to
        // the Exception that occurred in the DAO.
        getHibernateSession().clear();
    }
    
    @Test
    public final void testNewMultiSelectValid() throws Exception
    {
        final String key = "testNewMsVarValidKey";
        
        fMockMvc.perform(get(NewMultiSelectVarController.BASE_URL))
            .andExpect(status().isOk())
            .andExpect(model().attribute(
                    NewVarController.ATTRIBUTE_VARIABLE, isA(EditMultiSelectVar.class)));
        
        fMockMvc.perform(post(NewMultiSelectVarController.BASE_URL)
                .param("key", key)
                .param("displayName", "myDisplayName")
                .param("groupId", "1")
                .param("options[0]", "option1"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl(SrcalcUrls.MODEL_ADMIN_HOME));
        
        // Verify that the variable was actually created. Individual properties
        // are tested in EditMultiSelectVarTest.
        assertEquals(key, fAdminService.getVariable(key).getKey());
    }
    
    @Test
    public final void testNewMultiSelectNoOptions() throws Exception
    {
        fMockMvc.perform(post(NewMultiSelectVarController.BASE_URL)
                .param("key", "validKey")
                .param("displayName", "validDisplayName")
                .param("groupId", "1"))
            .andExpect(model().attributeHasErrors(NewVarController.ATTRIBUTE_VARIABLE));
    }
    
    @Test
    public final void testNewDiscreteNumericalValid() throws Exception
    {
        final String key = "testNewDnVarValidKey";
        final String category3Name = "category3";
        
        fMockMvc.perform(get(NewDiscreteNumericalVarController.BASE_URL))
            .andExpect(status().isOk())
            .andExpect(model().attribute(
                    NewVarController.ATTRIBUTE_VARIABLE,
                    isA(EditDiscreteNumericalVar.class)));
        
        fMockMvc.perform(post(NewDiscreteNumericalVarController.BASE_URL)
                .param("key", key)
                .param("displayName", "myDisplayName")
                .param("groupId", "1")
                .param("units", "g/dl")
                .param("validRange.lowerInclusive", "true")
                .param("validRange.lowerBound", "100.0")
                .param("validRange.upperBound", "150.0")
                .param("validRange.upperInclusive", "false")
                .param("categories[0].value", "category1")
                .param("categories[0].upperBound", "120.0")
                .param("categories[1].value", "category2")
                .param("categories[1].upperBound", "130.0")
                .param("categories[2].value", category3Name)
                .param("categories[2].upperBound", "150.0")
                // No value: this category should be omitted.
                .param("categories[4].upperBound", "160"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl(SrcalcUrls.MODEL_ADMIN_HOME));
        
        // Verify that the variable was actually created and verify that some of
        // the properties are correct. All properties are tested in
        // EditDiscreteNumericalVarTest.
        final DiscreteNumericalVariable var = 
                (DiscreteNumericalVariable)fAdminService.getVariable(key);
        assertEquals(key, var.getKey());
        assertEquals(category3Name, var.getCategories().last().getOption().getValue());
    }
    
    @Test
    public final void testNewDiscreteNumericalUnitsTooLong() throws Exception
    {
        fMockMvc.perform(post(NewDiscreteNumericalVarController.BASE_URL)
                .param("units", TestHelpers.stringOfLength(AbstractNumericalVariable.UNITS_MAX + 1)))
            .andExpect(view().name(Views.NEW_DISCRETE_NUMERICAL_VARIABLE))
            .andExpect(model().attributeHasFieldErrors(NewVarController.ATTRIBUTE_VARIABLE, "units"));
    }
}
