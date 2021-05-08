package com.groot.invoicify.service;

import com.groot.invoicify.dto.CompanyDto;
import com.groot.invoicify.entity.Company;
import com.groot.invoicify.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepos;

    public CompanyService(CompanyRepository companyRepos) {
        this.companyRepos = companyRepos;
    }

    public boolean create(CompanyDto companyDtoObject) {

        if (companyRepos.findByName(companyDtoObject.getName()) != null) {
            return false;
        } else {
            companyRepos.save(
                    new Company(companyDtoObject.getName(), companyDtoObject.getAddress(), companyDtoObject.getCity(),
                            companyDtoObject.getState(), companyDtoObject.getZip(), companyDtoObject.getContactName(),
                            companyDtoObject.getContactTitle(), companyDtoObject.getContactPhoneNumber()
                    )
            );
            return true;
        }
    }

    public List<CompanyDto> fetchAll() {

        return companyRepos.findAll()
                .stream()
                .map(companyEntity -> {
                    return new CompanyDto(
                            companyEntity.getName(),
                            companyEntity.getAddress(),
                            companyEntity.getCity(),
                            companyEntity.getState(),
                            companyEntity.getZip(),
                            companyEntity.getContactName(),
                            companyEntity.getContactTitle(),
                            companyEntity.getContactPhoneNumber()

                    );
                })
                .collect(Collectors.toList());
    }

    public CompanyDto findSingleCompany(String companyName) {

        Company companyFound = companyRepos.findByName(companyName);

        if (companyFound == null) {
            return null;
        }

        return new CompanyDto(companyFound.getName(), companyFound.getAddress(), companyFound.getCity(),
                companyFound.getState(), companyFound.getZip(), companyFound.getContactName(),
                companyFound.getContactTitle(), companyFound.getContactPhoneNumber());
    }

    public boolean patchCompany(long l, CompanyDto dts) {
        Optional<Company> companyOptional = companyRepos.findById(l);

        if (companyOptional.isPresent()) {
            Company companyEntity = companyOptional.get();
            companyEntity.setName(((dts.getName()!=null)? dts.getName():companyEntity.getName()));
            companyEntity.setAddress(((dts.getAddress()!=null)? dts.getAddress():companyEntity.getAddress()));
            companyEntity.setCity(((dts.getCity()!=null)? dts.getCity():companyEntity.getCity()));
            companyEntity.setState(((dts.getState()!=null)? dts.getState():companyEntity.getState()));
            companyEntity.setZip(((dts.getZip()!=null)? dts.getZip():companyEntity.getZip()));
            companyEntity.setContactName(((dts.getContactName()!=null)? dts.getContactName():companyEntity.getContactName()));
            companyEntity.setContactTitle(((dts.getContactTitle()!=null)? dts.getContactTitle():companyEntity.getContactTitle()));
            companyEntity.setContactPhoneNumber(((dts.getContactPhoneNumber()!=null)? dts.getContactPhoneNumber():companyEntity.getContactPhoneNumber()));
            companyRepos.save(companyEntity);

            return true;

        } else {

            return false;
        }
    }
}
