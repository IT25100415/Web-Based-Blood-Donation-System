package com.bloodbank.config;

import com.bloodbank.model.Donor;
import com.bloodbank.model.Hospital;
import com.bloodbank.model.Organizer;
import com.bloodbank.model.User;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.HospitalRepository;
import com.bloodbank.repository.OrganizerRepository;
import com.bloodbank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createUser("admin@bloodbank.org", "Admin", User.Role.ADMIN);
        createUser("staff@bloodbank.org", "Staff Member", User.Role.STAFF);
        createUser("hospital@bloodbank.org", "City Hospital", User.Role.HOSPITAL);
        createUser("organizer@bloodbank.org", "Drive Organizer", User.Role.ORGANIZER);
        createUser("donor@bloodbank.org", "Jane Donor", User.Role.DONOR);
    }

    private void createUser(String email, String name, User.Role role) {
        User u = userRepository.findByEmail(email);
        if (u == null) {
            u = new User();
            u.setEmail(email);
            u.setName(name);
            u.setPassword(passwordEncoder.encode("password123"));
            u.setRole(role);
            u = userRepository.save(u);
        }
        
        if (role == User.Role.DONOR) {
            if (!donorRepository.existsById(u.getId())) {
                Donor d = new Donor();
                d.setUser(u);
                d.setNic("123456789V");
                d.setBloodGroup("O+");
                d.setCity("Colombo");
                d.setPhone("0712345678");
                donorRepository.save(d);
            }
        } else if (role == User.Role.HOSPITAL) {
            if (!hospitalRepository.existsById(u.getId())) {
                Hospital h = new Hospital();
                h.setUser(u);
                h.setHospitalName(name);
                h.setAddress("123 Health Ave, Colombo");
                h.setEmergencyContact("0112345678");
                hospitalRepository.save(h);
            }
        } else if (role == User.Role.ORGANIZER) {
            if (!organizerRepository.existsById(u.getId())) {
                Organizer o = new Organizer();
                o.setUser(u);
                o.setOrganizationName(name + " Community");
                o.setContactPerson(name);
                o.setPhone("0119876543");
                organizerRepository.save(o);
            }
        }
    }
}
