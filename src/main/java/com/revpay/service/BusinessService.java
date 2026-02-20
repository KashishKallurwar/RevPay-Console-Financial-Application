package com.revpay.service;

import java.util.Scanner;
import com.revpay.dao.BusinessDao;
import com.revpay.dao.BusinessDaoImpl;

public class BusinessService {

    public void registerBusiness(int userId) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Business Name: ");
        String name = sc.nextLine();

        System.out.print("Business Type: ");
        String type = sc.nextLine();

        System.out.print("Tax ID: ");
        String taxId = sc.nextLine();

        System.out.print("Address: ");
        String address = sc.nextLine();
        
        System.out.print("Verification Document (File Name): ");
        String doc = sc.nextLine();

        BusinessDao dao = new BusinessDaoImpl();
        dao.registerBusiness(userId, name, type, taxId, address);

        System.out.println("✅ Business registered successfully");
    }
}
