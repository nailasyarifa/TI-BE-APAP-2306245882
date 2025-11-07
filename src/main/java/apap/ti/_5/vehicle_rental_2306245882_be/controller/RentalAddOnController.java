// package apap.ti._5.vehicle_rental_2306245882_be.controller;

// import apap.ti._5.vehicle_rental_2306245882_be.model.RentalAddOn;
// import apap.ti._5.vehicle_rental_2306245882_be.service.BadRequestException;
// import apap.ti._5.vehicle_rental_2306245882_be.service.RentalAddOnService;
// import apap.ti._5.vehicle_rental_2306245882_be.service.ResourceNotFoundException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import java.util.List;

// @Controller
// @RequestMapping("/addons")
// public class RentalAddOnController {

//     private final RentalAddOnService addOnService;

//     @Autowired
//     public RentalAddOnController(RentalAddOnService addOnService) {
//         this.addOnService = addOnService;
//     }

//     // LIST all add-ons
//     @GetMapping({"", "/"})
//     public String viewAllAddOns(Model model) {
//         List<RentalAddOn> addOns = addOnService.getAll();
//         model.addAttribute("addOns", addOns);
//         return "addons/addons"; // templates/addons/addons.html
//     }

//     // SHOW create form
//     @GetMapping("/create")
//     public String showCreateForm(Model model) {
//         if (!model.containsAttribute("rentalAddOn")) {
//             model.addAttribute("rentalAddOn", new RentalAddOn());
//         }
//         return "addons/create"; // templates/addons/create.html
//     }

//     // HANDLE create submit
//     @PostMapping("/create")
//     public String createAddOn(@ModelAttribute("rentalAddOn") RentalAddOn rentalAddOn,
//                               RedirectAttributes ra) {
//         try {
//             RentalAddOn saved = addOnService.create(rentalAddOn);
//             ra.addFlashAttribute("success", "Add-on berhasil ditambahkan: " + saved.getId());
//             return "redirect:/addons";
//         } catch (BadRequestException e) {
//             ra.addFlashAttribute("error", e.getMessage());
//             ra.addFlashAttribute("rentalAddOn", rentalAddOn);
//             return "redirect:/addons/create";
//         } catch (Exception e) {
//             ra.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
//             ra.addFlashAttribute("rentalAddOn", rentalAddOn);
//             return "redirect:/addons/create";
//         }
//     }

//     // DETAIL
//     @GetMapping("/{id}")
//     public String viewAddOnDetail(@PathVariable("id") String id, Model model, RedirectAttributes ra) {
//         try {
//             RentalAddOn addon = addOnService.getById(id);
//             model.addAttribute("addOn", addon);
//             return "addons/detail"; // templates/addons/detail.html
//         } catch (ResourceNotFoundException e) {
//             ra.addFlashAttribute("error", e.getMessage());
//             return "redirect:/addons";
//         }
//     }

//     // DELETE
//     @PostMapping("/{id}/delete")
//     public String deleteAddOn(@PathVariable("id") String id, RedirectAttributes ra) {
//         try {
//             addOnService.delete(id);
//             ra.addFlashAttribute("success", "Add-on berhasil dihapus.");
//         } catch (ResourceNotFoundException e) {
//             ra.addFlashAttribute("error", e.getMessage());
//         } catch (Exception e) {
//             ra.addFlashAttribute("error", "Gagal menghapus add-on: " + e.getMessage());
//         }
//         return "redirect:/addons";
//     }
// }
