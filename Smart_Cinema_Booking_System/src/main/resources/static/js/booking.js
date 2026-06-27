//------------------- BOOKING -----------------------//

let selectedShowtimeId = null;
let ticketPrice = 0;
let selectedSeats = new Set();
let currentVipSeats = new Set();
let currentBookingId = null;
let holdExpiredAt = null;
let countdownInterval = null;

// chọn xuất chiếu & chọn ghế
document.addEventListener("click", function (e) {
    // chọn xuất chiếu
    if (e.target.closest(".showtime-btn")) {

        if (currentBookingId != null) {
                alert("Bạn đang giữ ghế. Vui lòng thanh toán hoặc chờ hết hạn.");
                return;
        }

        const btn = e.target.closest(".showtime-btn"); // Từ phần tử vừa được click, tìm ngược lên các thẻ cha gần nhất có class .showtime-btn

        selectedShowtimeId = btn.dataset.showtimeId;
        ticketPrice = Number(btn.dataset.ticketPrice);
        currentVipSeats = parseSeatList(btn.dataset.vipSeats);

        document.querySelectorAll(".showtime-btn")
            .forEach(b => b.classList.remove("border-yellow-500", "bg-yellow-50"));

        btn.classList.add("border-yellow-500", "bg-yellow-50");

        renderSeats(btn);
        updateSummary();
        loadSeatsStatus(selectedShowtimeId);
    }

    // chọn ghế
    if (e.target.classList.contains("seat-btn")) {

        if (currentBookingId != null) {
                alert("Bạn đang giữ ghế. Vui lòng thanh toán hoặc chờ hết hạn.");
                return;
        }

        const seatCodes = e.target.dataset.seat.split(",");

        const isSelected = seatCodes.every(seat => selectedSeats.has(seat));

        if (isSelected) {
            seatCodes.forEach(seat => selectedSeats.delete(seat));
            e.target.classList.remove("bg-yellow-400");
        } else {
            seatCodes.forEach(seat => selectedSeats.add(seat));
            e.target.classList.add("bg-yellow-400");
        }

        updateSummary();
    }

    if (e.target.id === "confirmBookingBtn") {
        confirmBooking();
    }
});

// render ghế
function renderSeats(btn) {
    const seatMap = document.getElementById("seatMap");

    const seatsX = Number(btn.dataset.seatsX);
    const seatsY = Number(btn.dataset.seatsY);
    const totalSeats = Number(btn.dataset.totalSeats);

    const vipSeats = parseSeatList(btn.dataset.vipSeats);
    const coupleSeats = parseCoupleSeats(btn.dataset.coupleSeats);

    seatMap.innerHTML = "";
    seatMap.style.gridTemplateColumns = `repeat(${seatsX}, minmax(44px, 1fr))`;

    selectedSeats.clear();

    let renderedSeatCount = 0;

    for (let row = 0; row < seatsY; row++) {
        for (let col = 1; col <= seatsX; col++) {
            if (renderedSeatCount >= totalSeats) {
                return;
            }

            const seatCode = String.fromCharCode(65 + row) + col;

            if (coupleSeats.has(seatCode)) {
                const pair = getCouplePair(seatCode, btn.dataset.coupleSeats);

                if (pair && pair.first !== seatCode) {
                    continue;
                }

                const coupleBtn = document.createElement("button");
                coupleBtn.type = "button";
                coupleBtn.dataset.seat = pair ? `${pair.first},${pair.second}` : seatCode;
                coupleBtn.textContent = pair ? `${pair.first}-${pair.second}` : seatCode;
                coupleBtn.className = "seat-btn couple-seat border rounded py-2 bg-pink-100 text-pink-700 hover:bg-pink-200 col-span-2";

                seatMap.appendChild(coupleBtn);

                renderedSeatCount += 2;
                col += 1;
                continue;
            }

            const seatBtn = document.createElement("button");
            seatBtn.type = "button";
            seatBtn.dataset.seat = seatCode;
            seatBtn.textContent = seatCode;

            if (vipSeats.has(seatCode)) {
                seatBtn.className = "seat-btn vip-seat border rounded py-2 bg-blue-100 text-blue-700 hover:bg-blue-200";
            } else {
                seatBtn.className = "seat-btn normal-seat border rounded py-2 bg-white hover:bg-yellow-100";
            }

            seatMap.appendChild(seatBtn);
            renderedSeatCount++;
        }
    }
}

function parseSeatList(value) {
    if (!value) {
        return new Set();
    }

    return new Set(
        value.split(",")
            .map(seat => seat.trim())
            .filter(seat => seat !== "")
    );
}

function parseCoupleSeats(value) {
    const result = new Set();

    if (!value) {
        return result;
    }

    value.split(",").forEach(pair => {
        const seats = pair.trim().split("-");

        if (seats.length === 2) {
            result.add(seats[0].trim());
            result.add(seats[1].trim());
        }
    });

    return result;
}

function getCouplePair(seatCode, value) {
    if (!value) {
        return null;
    }

    const pairs = value.split(",");

    for (const pairText of pairs) {
        const seats = pairText.trim().split("-");

        if (seats.length !== 2) {
            continue;
        }

        const first = seats[0].trim();
        const second = seats[1].trim();

        if (first === seatCode || second === seatCode) {
            return { first, second };
        }
    }

    return null;
}

function loadSeatsStatus(showtimeId) {
    fetch("/user/booking/seats-status/" + showtimeId)
        .then(res => res.json())
        .then(lockedSeats => {
            lockedSeats.forEach(({ seatCode, status }) => {
                document.querySelectorAll(".seat-btn").forEach(btn => {
                    const codes = btn.dataset.seat.split(",");
                    if (codes.includes(seatCode)) {
                        if (status === "PENDING") {
                            btn.className = btn.className
                                .replace(/bg-\S+/g, "")
                                .replace(/hover:bg-\S+/g, ""
                            ).trim() + " bg-orange-300 text-orange-800 cursor-not-allowed opacity-70";
                        } else if (status === "PAID") {
                            btn.className = btn.className
                                .replace(/bg-\S+/g, "")
                                .replace(/hover:bg-\S+/g, ""
                            ).trim() + " bg-red-400 text-white cursor-not-allowed opacity-70";
                        }
                        btn.disabled = true;
                    }
                });
            });
        });
}

// tính tiền
function updateSummary() {
    const seats = Array.from(selectedSeats);

    document.getElementById("selectedSeats").innerText =
        seats.length > 0 ? seats.join(", ") : "Chưa chọn";

    let total = 0;

    seats.forEach(seat => {
        if (currentVipSeats.has(seat)) {
            total += ticketPrice + 50000;
        } else {
            total += ticketPrice;
        }
    });

    document.getElementById("totalAmount").innerText =
        total.toLocaleString("vi-VN") + " VND";
}

function confirmBooking() {
    const payload = {
        showtimeId: selectedShowtimeId,
        seatCodes: Array.from(selectedSeats),
        paymentMethod: document.getElementById("paymentMethod").value
    };

    console.log(payload);
}

// nút xác nhận để giữ ghế
document.addEventListener("click", function (e) {
    if (e.target.id === "bookingActionBtn") {
        if (currentBookingId == null) {
            holdSeats();
        } else {
            payBooking();
        }
    }
});

function holdSeats() {
    if (!selectedShowtimeId) {
        alert("Vui lòng chọn suất chiếu");
        return;
    }

    if (selectedSeats.size === 0) {
        alert("Vui lòng chọn ghế");
        return;
    }

    const payload = {
        showtimeId: selectedShowtimeId,
        seatCodes: Array.from(selectedSeats),
        paymentMethod: document.getElementById("paymentMethod").value
    };

    fetch("/user/booking/hold", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
    .then(async response => {
        if (!response.ok) {
            const message = await response.text();
            throw new Error(message);
        }

        return response.json();
    })
    .then(data => {
        currentBookingId = data.bookingId;
        holdExpiredAt = data.expiredAt;

        const actionBtn = document.getElementById("bookingActionBtn");
        actionBtn.innerText = "Thanh toán";
        actionBtn.className = "w-full bg-green-500 hover:bg-green-600 rounded-lg py-3 font-bold";
        startCountdown(29);
    })
    .catch(error => {
        alert(error.message);
    });
}

function startCountdown(seconds) {
    const box = document.getElementById("holdCountdown");
    const display = document.getElementById("countdownSeconds");
    box.classList.remove("hidden");
    display.innerText = seconds;

    countdownInterval = setInterval(() => {
        seconds--;
        display.innerText = seconds;
        if (seconds <= 0) {
            clearCountdown();
            currentBookingId = null;
            holdExpiredAt = null;
            const expBtn = document.getElementById("bookingActionBtn");
            expBtn.innerText = "Xác nhận giữ ghế";
            expBtn.className = "w-full bg-yellow-500 hover:bg-yellow-600 rounded-lg py-3 font-bold";
            alert("Hết thời gian giữ ghế!");
        }
    }, 1000);
}

function clearCountdown() {
    clearInterval(countdownInterval);
    countdownInterval = null;
    const box = document.getElementById("holdCountdown");
    if (box) box.classList.add("hidden");
}

function payBooking() {
    fetch("/user/booking/pay/" + currentBookingId, {
        method: "POST"
    })
    .then(async response => {
        if (!response.ok) {
            const message = await response.text();
            throw new Error(message);
        }

        return response.text();
    })
    .then(() => {
        alert("Thanh toán thành công");

        currentBookingId = null;
        holdExpiredAt = null;
        clearCountdown();

        const paidBtn = document.getElementById("bookingActionBtn");
        paidBtn.innerText = "Xác nhận giữ ghế";
        paidBtn.className = "w-full bg-yellow-500 hover:bg-yellow-600 rounded-lg py-3 font-bold";

        selectedSeats.clear();
        updateSummary();

        loadContent("/user/booking");
    })
    .catch(error => {
        alert(error.message);
    });
}