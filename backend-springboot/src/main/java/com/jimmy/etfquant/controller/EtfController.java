package com.jimmy.etfquant.controller;

import com.jimmy.etfquant.dto.EtfResponse;
import com.jimmy.etfquant.service.EtfService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EtfController {

    private final EtfService etfService;

    public EtfController(EtfService etfService) {
        this.etfService = etfService;
    }

    @GetMapping("/api/etfs")
    public List<EtfResponse> getAllEtfs() {
        return etfService.getAllEtfs();
    }
}