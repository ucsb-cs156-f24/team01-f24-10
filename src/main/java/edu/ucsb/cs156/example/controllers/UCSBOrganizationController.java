package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


/**
 * This is a REST controller for UCSBOrganization
 */

@Tag(name = "UCSBOrganizations")
@RequestMapping("/api/ucsborganizations")
@RestController
@Slf4j
public class UCSBOrganizationController extends ApiController {

    @Autowired
    UCSBOrganizationRepository ucsbOrganizationRepository;

    /**
     * THis method returns a list of all ucsborganizations.
     * @return a list of all ucsborganizations
     */
    @Operation(summary= "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganization> allOrganisations() {
        Iterable<UCSBOrganization> organizations = ucsbOrganizationRepository.findAll();
        return organizations;
    }

    /**
     * This method returns a single ucsborganization.
     * @param orgCode code of the ucsborganization
     * @return a single ucsborganization
     */
    @Operation(summary= "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganization getById(
        @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganization organization = ucsbOrganizationRepository.findById(orgCode)
            .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));

        return organization;
    }

    /**
     * This method creates a new ucsborganizations. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode code of the ucsborganizations
     * @param orgTranslationShort short name of the organization
     * @param orgTranslation full name of the organization
     * @param inactive whether or not the organization is inactive
     * @return the saved ucsborganizations
     */

    @Operation(summary= "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganization postOrganisations(
        @Parameter(name="orgCode") @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
        @Parameter(name="inactive") @RequestParam boolean inactive
        )
        {

        UCSBOrganization organization = new UCSBOrganization();
        organization.setOrgCode(orgCode);
        organization.setOrgTranslationShort(orgTranslationShort);
        organization.setOrgTranslation(orgTranslation);
        organization.setInactive(inactive);

        UCSBOrganization savedOrganization = ucsbOrganizationRepository.save(organization);

        return savedOrganization;
    }

    /**
     * Update a single ucsborganization. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode code of the organization
     * @param incoming the new organization contents
     * @return the updated organization object
     */
    @Operation(summary= "Update a single organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganization updateCommons(
            @Parameter(name="orgCode") @RequestParam String orgCode,
            @RequestBody @Valid UCSBOrganization incoming) {

        UCSBOrganization organization = ucsbOrganizationRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommons.class, orgCode));


        organization.setOrgTranslationShort(incoming.getOrgTranslationShort());
        organization.setOrgTranslation(incoming.getOrgTranslation());
        organization.setInactive(incoming.getInactive());

        ucsbOrganizationRepository.save(organization);

        return organization;
    }
}