"use strict";

document.addEventListener("DOMContentLoaded", function () {
    const menuButton = document.querySelector(".menu-toggle");
    const closeButton = document.querySelector(".menu-close");
    const navigation = document.querySelector(".global-nav");
    const overlay = document.querySelector(".menu-overlay");

    if (!menuButton || !navigation) {
        return;
    }

    function getFocusableElements() {
        return Array.from(
            navigation.querySelectorAll(
                'a[href], button:not([disabled])'
            )
        );
    }

    function openMenu() {
        menuButton.setAttribute("aria-expanded", "true");
        menuButton.setAttribute("aria-label", "メニューを閉じる");

        navigation.setAttribute("aria-hidden", "false");
        navigation.classList.add("is-open");

        if (overlay) {
            overlay.classList.add("is-open");
        }

        document.body.classList.add("menu-open");

        if (closeButton) {
            closeButton.focus();
        }
    }

    function closeMenu(returnFocus) {
        menuButton.setAttribute("aria-expanded", "false");
        menuButton.setAttribute("aria-label", "メニューを開く");

        navigation.setAttribute("aria-hidden", "true");
        navigation.classList.remove("is-open");

        if (overlay) {
            overlay.classList.remove("is-open");
        }

        document.body.classList.remove("menu-open");

        if (returnFocus) {
            menuButton.focus();
        }
    }

    function toggleMenu() {
        const isOpen =
            menuButton.getAttribute("aria-expanded") === "true";

        if (isOpen) {
            closeMenu(false);
        } else {
            openMenu();
        }
    }

    menuButton.addEventListener("click", function () {
        toggleMenu();
    });

    if (closeButton) {
        closeButton.addEventListener("click", function () {
            closeMenu(true);
        });
    }

    if (overlay) {
        overlay.addEventListener("click", function () {
            closeMenu(true);
        });
    }

    navigation.addEventListener("click", function (event) {
        const clickedLink = event.target.closest("a");

        if (clickedLink) {
            closeMenu(false);
        }
    });

    document.addEventListener("keydown", function (event) {
        const isOpen =
            menuButton.getAttribute("aria-expanded") === "true";

        if (!isOpen) {
            return;
        }

        if (event.key === "Escape") {
            closeMenu(true);
            return;
        }

        if (event.key !== "Tab") {
            return;
        }

        const focusableElements = getFocusableElements();

        if (focusableElements.length === 0) {
            return;
        }

        const firstElement = focusableElements[0];
        const lastElement =
            focusableElements[focusableElements.length - 1];

        if (
            event.shiftKey &&
            document.activeElement === firstElement
        ) {
            event.preventDefault();
            lastElement.focus();
        }

        if (
            !event.shiftKey &&
            document.activeElement === lastElement
        ) {
            event.preventDefault();
            firstElement.focus();
        }
    });
});