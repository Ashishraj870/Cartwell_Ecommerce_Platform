package com.shop.entity;
/** Order of constants matters: status may only move forward (or to CANCELLED). */
public enum OrderStatus { PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
