package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.exception.FieldException;
import com.example.smart_cinema_booking_system.model.dto.ShowtimeRequest;
import com.example.smart_cinema_booking_system.model.dto.ShowtimeResponse;
import com.example.smart_cinema_booking_system.model.entity.Movie;
import com.example.smart_cinema_booking_system.model.entity.Showtime;
import com.example.smart_cinema_booking_system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository movieRepo;
    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepo;

    // get all showtimes
    public Page<Showtime> getShowtimes(String keyword, Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            return showtimeRepo.findAll(pageable);
        }

        return showtimeRepo.search(keyword, pageable);
    }

    // create showtime
    public void save(ShowtimeRequest req) {
        Long conflict = showtimeRepo.countConflict(req.getRoomId(),req.getStartTime(),req.getEndTime());
        System.out.println(conflict);
        System.out.println(req.getRoomId()+" "+req.getStartTime() +" "+req.getStartTime());
        if (conflict>0) {
            throw new FieldException(
                    "roomId",
                    "Đang có xuất chiếu vào giờ này"
            );
        }
        Showtime showtime = (req.getShowtimeId() != null)
                 ? showtimeRepo.findById(req.getShowtimeId()).orElse(new Showtime())
                 : new Showtime();

        showtime.setMovie(
                movieRepo.findById(req.getMovieId()).orElseThrow(() -> new RuntimeException("Không tìm thấy phim"))
        );
        showtime.setRoom(
                roomRepo.findById(req.getRoomId()).orElseThrow(()-> new RuntimeException("không tìm thấy phòng"))
        );
        showtime.setStartTime(req.getStartTime());
        showtime.setEndTime(req.getEndTime());
        showtime.setStatus(req.getStatus());
        showtime.setTicketPrice(req.getTicketPrice());

        showtimeRepo.save(showtime);
    }

    // delete showtime
    public void delete(Long showtimeId) {

        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xuất chiếu"));

        if (bookingRepo.existsByShowtimeShowtimeId(showtimeId)) {
            throw new RuntimeException(
                    "Xuất chiếu đã có người đặt vé. Không thể xóa."
            );
        }

        showtimeRepo.delete(showtime);
    }

    //
    public Showtime findById(Long id){
        return showtimeRepo.findById(id).orElseThrow(() ->
                new RuntimeException("không tìm thấy xuất chiếu"));
    }

    // get all movie
    public List<Showtime> getAllMovies(){
        return showtimeRepo.findAll();
    }

    // get respon
    public ShowtimeResponse getResponseById(Long id){

        Showtime showtime = showtimeRepo.findById(id).orElseThrow();

        ShowtimeResponse response = new ShowtimeResponse();

        response.setShowtimeId(showtime.getShowtimeId());
        response.setMovieId(showtime.getMovie().getMovieId());
        response.setRoomId(showtime.getRoom().getRoomId());
        response.setStartTime(showtime.getStartTime());
        response.setEndTime(showtime.getEndTime());
        response.setStatus(showtime.getStatus());
        response.setTicketPrice(showtime.getTicketPrice());

        return response;
    }
}
