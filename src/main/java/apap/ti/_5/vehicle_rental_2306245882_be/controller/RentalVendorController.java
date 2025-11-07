// package apap.ti._5.vehicle_rental_2306245882_be.controller;

// import apap.ti._5.vehicle_rental_2306245882_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
// import apap.ti._5.vehicle_rental_2306245882_be.service.RentalVendorService;
// import apap.ti._5.vehicle_rental_2306245882_be.service.ResourceNotFoundException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import java.util.List;

// @Controller
// @RequestMapping("/vendors")
// public class RentalVendorController {

//     private final RentalVendorService vendorService;

//     @Autowired
//     public RentalVendorController(RentalVendorService vendorService) {
//         this.vendorService = vendorService;
//     }

//     // LIST all vendors
//     @GetMapping({"", "/"})
//     public String viewAllVendors(Model model) {
//         List<RentalVendor> vendors = vendorService.getAll();
//         model.addAttribute("vendors", vendors);
//         return "vendors/vendors"; // templates/vendors/vendors.html
//     }

//     // SHOW create form
//     @GetMapping("/create")
//     public String showCreateForm(Model model) {
//         if (!model.containsAttribute("rentalVendor")) {
//             model.addAttribute("rentalVendor", new RentalVendor());
//         }
//         return "vendors/create"; // templates/vendors/create.html
//     }

//     // HANDLE create submit
//     @PostMapping("/create")
//     public String createVendor(@ModelAttribute("rentalVendor") RentalVendor rentalVendor,
//                                RedirectAttributes ra) {
//         try {
//             RentalVendor saved = vendorService.create(rentalVendor);
//             ra.addFlashAttribute("success", "Vendor berhasil ditambahkan: " + saved.getId());
//             return "redirect:/vendors";
//         } catch (BadRequestException e) {
//             ra.addFlashAttribute("error", e.getMessage());
//             ra.addFlashAttribute("rentalVendor", rentalVendor);
//             return "redirect:/vendors/create";
//         } catch (Exception e) {
//             ra.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
//             ra.addFlashAttribute("rentalVendor", rentalVendor);
//             return "redirect:/vendors/create";
//         }
//     }

//     // DETAIL
//     @GetMapping("/{id}")
//     public String viewVendorDetail(@PathVariable("id") String id, Model model, RedirectAttributes ra) {
//         try {
//             RentalVendor vendor = vendorService.getById(id);
//             model.addAttribute("vendor", vendor);
//             return "vendors/detail"; // templates/vendors/detail.html
//         } catch (ResourceNotFoundException e) {
//             ra.addFlashAttribute("error", e.getMessage());
//             return "redirect:/vendors";
//         }
//     }

//     // DELETE
//     @PostMapping("/{id}/delete")
//     public String deleteVendor(@PathVariable("id") String id, RedirectAttributes ra) {
//         try {
//             vendorService.delete(id);
//             ra.addFlashAttribute("success", "Vendor berhasil dihapus.");
//         } catch (ResourceNotFoundException e) {
//             ra.addFlashAttribute("error", e.getMessage());
//         } catch (Exception e) {
//             ra.addFlashAttribute("error", "Gagal menghapus vendor: " + e.getMessage());
//         }
//         return "redirect:/vendors";
//     }
// }
