/**
 * <p>
 * Data mapping between the Domain Model and VistA instances. Various DAOs (Data Access
 * Objects) provide the API for this data mapping.
 * </p>
 * 
 * <p>
 * All DAOs are Java interfaces with a corresponding Remote Procedure-based
 * implementation. The interfaces exist to facilitate mocking for integration tests.
 * </p>
 * 
 * <p>
 * Although our VistA integration technology is VistALink, the code in this
 * package is not specific to VistALink. All VistALink-specific code is
 * contained in the {@link gov.va.med.srcalc.vista.vistalink} sub-package.
 * </p>
 * 
 * <p>
 * See the {@link gov.va.med.srcalc.vista.RemoteProcedure RemoteProcedure} enumeration for
 * a list of all VistA Remote Procedures used by the application.
 * </p>
 */
package gov.va.med.srcalc.vista;