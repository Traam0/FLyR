package shared.components;

import core.navigation.Router;
import mvvm.views.LoginView;
import mvvm.views.flight.FlightSearchView;
import mvvm.views.reservations.ReservationsView;
import shared.common.MaterialColors;
import shared.layouts.FlexPanelH;
import shared.layouts.FlexPanelV;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

public class TopBar extends FlexPanelH {
    private final Router router;
    private final Logger logger;

    private TopBar(Router router, Logger logger) {
        this.router = router;
        this.setGap(10);

        this.logger = logger;
        var searchBtn = new Button("Search", MaterialColors.GREY_50, MaterialColors.BLACK, 30, 12);
        searchBtn.addActionListener(this::onSearchBtnClick);
        this.add(searchBtn);
        var reservationsBtn = new Button("My Reservation", MaterialColors.GREY_50, MaterialColors.BLACK, 30, 12);
        reservationsBtn.addActionListener(this::onReservationBtnClick);
        this.add(reservationsBtn);
        var logoutBtn = new Button("Logout", MaterialColors.RED_500, MaterialColors.WHITE, 30, 12);
        logoutBtn.addActionListener(this::onLogout);
        this.add(logoutBtn);
        this.setOpaque(false);
        this.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MaterialColors.BLACK),
                new EmptyBorder(5, 15, 5, 15)
        ));
    }

    private void onLogout(ActionEvent e) {
        this.router.getContext().logout();
        this.router.navigateTo(LoginView.class);
    }

    private void onReservationBtnClick(ActionEvent e) {
        this.logger.info("Reservation Button clicked");
        this.router.navigateTo(ReservationsView.class);
        //TODO
    }

    private void onSearchBtnClick(ActionEvent e) {
        this.logger.info("Search Button clicked");
        this.router.navigateTo(FlightSearchView.class);
    }

    public static TopBar build(Router router, Logger logger) {
        return new TopBar(router, logger);
    }
}
