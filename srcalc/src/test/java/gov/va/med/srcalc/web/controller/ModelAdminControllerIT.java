package gov.va.med.srcalc.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import gov.va.med.srcalc.test.util.TestNameLogger;

import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration  // need to tell Spring to instantiate a WebApplicationContext.
@ContextConfiguration({"/srcalc-context.xml", "/srcalc-controller.xml", "/test-context.xml"})
public class ModelAdminControllerIT
{
    private static final Logger fLogger = LoggerFactory.getLogger(ModelAdminControllerIT.class);
    
    @Autowired
    WebApplicationContext fWac;

    private MockMvc fMockMvc;
    
    @Rule
    public final TestRule fTestLogger = new TestNameLogger(fLogger);

    @Before
    public void setup()
    {
        fMockMvc = MockMvcBuilders.webAppContextSetup(fWac).build();
    }
    
    @Test
    @Transactional
    public final void testDefaultPage() throws Exception
    {
        fMockMvc.perform(get("/admin/models")).
            andExpect(model().attributeExists("variables"));
    }
    
}