/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.ms.model;

/**
 *
 * @author CB205360
 */
public class Peak {

    protected double mz = 0;
    protected double intensity = 0;
    protected MSMSSpectrum spectrum = null;

    protected int charge = 0;

    /**
     * the constructor is declared protected to prevent direct instantiation of this class. Please use methods in the Spectrum class instead.
     *
     * @param moz : peak mass (m/z)
     * @param intensity : peak intensity
     */
    protected Peak(double moz, double intensity, int charge, MSMSSpectrum owner) {
        this.mz = moz;
        this.intensity = intensity;
        this.charge = charge;
        this.spectrum = owner;
    }

    protected Peak(double moz, double intensity, MSMSSpectrum owner) {
        this(moz, intensity, 0, owner);
    }

    public double getMz() {
        return mz;
    }

    public int getCharge() {
        return charge;
    }

    public double getIntensity() {
        return intensity;
    }

    public MSMSSpectrum getSpectrum() {
        return spectrum;
    }

}
