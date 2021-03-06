package gov.va.med.srcalc.web.controller.admin;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import gov.va.med.srcalc.service.ModelInspectionService;
import gov.va.med.srcalc.test.util.IntegrationTest;

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

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

/**
 * Integration Test for {@link EditProceduresController}. Note that we only define
 * Integration Tests for controllers because unit tests are of little value.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration  // need to tell Spring to instantiate a WebApplicationContext.
@ContextConfiguration({"/srcalc-context.xml", "/srcalc-controller.xml", "/test-context.xml"})
@Transactional // run each test in its own (rolled-back) transaction
public class EditProceduresControllerIT extends IntegrationTest
{
    @Autowired
    WebApplicationContext fWac;
    
    @Autowired
    ModelInspectionService fModelService;

    private MockMvc fMockMvc;

    @Before
    public void setup()
    {
        fMockMvc = MockMvcBuilders.webAppContextSetup(fWac).build();
    }
    
    @Test
    public final void testProcedureDisplay() throws Exception
    {
        fMockMvc.perform(get(EditProceduresController.BASE_URL))
            .andExpect(status().isOk())
            .andExpect(model().attribute(
                    EditProceduresController.ATTRIBUTE_PROCEDURES,
                    fModelService.getAllProcedures()));
    }
    
    @Test
    public final void testProcedureUploadValid() throws Exception
    {
        final ByteSource sampleUpload = Resources.asByteSource(
                ProcedureRowTranslatorTest.VALID_PROCEDURES_RESOURCE);

        fMockMvc.perform(fileUpload(EditProceduresController.BASE_URL)
                .file("newProceduresFile", sampleUpload.read()))
            .andExpect(redirectedUrl(EditProceduresController.BASE_URL))
            .andExpect(flash().attributeExists(
                    EditProceduresController.FLASH_ATTR_SUCCESS));
        
        // Verify that the procedures were actually updated.
        assertEquals(
                ProcedureRowTranslatorTest.VALID_PROCEDURES,
                fModelService.getAllProcedures());
    }
    
    @Test
    public final void testSampleUploadResource() throws Exception
    {
        fMockMvc.perform(get("/admin/resources/sample_procedures_upload.csv"))
            .andExpect(status().isOk());
    }
    
}
