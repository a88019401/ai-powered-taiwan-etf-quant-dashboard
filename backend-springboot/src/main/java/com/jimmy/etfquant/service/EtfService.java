package com.jimmy.etfquant.service;

import com.jimmy.etfquant.dto.EtfResponse;
import com.jimmy.etfquant.repository.EtfRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtfService {

    private final EtfRepository etfRepository;

    public EtfService(EtfRepository etfRepository) {
        this.etfRepository = etfRepository;
    }

    public List<EtfResponse> getAllEtfs() {
        return etfRepository.findAllEtfs();
    }
}