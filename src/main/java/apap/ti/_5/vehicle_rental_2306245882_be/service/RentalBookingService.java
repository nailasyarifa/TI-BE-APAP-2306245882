package apap.ti._5.vehicle_rental_2306245882_be.service;

import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.UpdateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245882_be.restdto.request.CreateRentalBookingRequestDTO;

import java.util.List;
import java.util.Map;

import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.BookingListItem;
import apap.ti._5.vehicle_rental_2306245882_be.dto.rental_booking.SearchResultVehicleDTO;
import apap.ti._5.vehicle_rental_2306245882_be.model.RentalBooking;

public interface RentalBookingService {
    List<BookingListItem> getAllBookingsForList();
    RentalBooking getById(String id);
    RentalBooking createRentalBooking(CreateRentalBookingRequestDTO req) throws BadRequestException;
    List<SearchResultVehicleDTO> findAvailableVehicles(CreateRentalBookingRequestDTO req);
    RentalBooking updateBookingDetails(UpdateRentalBookingRequestDTO dto);
    RentalBooking updateBookingStatus(String id, String newStatus) throws BadRequestException;
    RentalBooking updateAddOns(UpdateRentalBookingRequestDTO dto) throws BadRequestException;
    RentalBooking cancelBooking(String id) throws BadRequestException;
    Map<String, Object> getBookingStats(String period, int year);

}