/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.profi.ms.model;

import fr.profi.ms.Constants;

import java.util.*;

/**
 *
 * @author CB205360
 */
public class MSMSSpectrum {

    private Map<String, Object> annotations;
    protected Peak precursor;
    protected double retentionTime;
    
    List<Peak> peaks;
    
    protected MSMSSpectrum(MSMSSpectrum sp) {
        this(sp.getPrecursorMz(), sp.getPrecursorIntensity(), sp.getPrecursorCharge(), sp.getRetentionTime());
    }

    public MSMSSpectrum(double MHParent, double intensityParent, int chargeParent) {
        this(MHParent, intensityParent, chargeParent, Double.MIN_VALUE);
    }

    public MSMSSpectrum(double precursorMz, double intensityParent, int charge, double retentionTime) {
        this.precursor = new Peak(precursorMz, intensityParent, charge, this);
        this.retentionTime = retentionTime;
        this.peaks = new ArrayList<Peak>();
    }

    public double getPrecursorMz() {
        return precursor.getMz();
    }
    
    public double getPrecursorMass() {
        return Constants.convertMzToMH(precursor.getMz(), precursor.getCharge());
    }
    
    public double getPrecursorIntensity() {
        return precursor.getIntensity();
    }

    public int getPrecursorCharge() {
        return precursor.getCharge();
    }
    
    public double getRetentionTime() {
        return retentionTime;
    }
    
    public double getRetentionTimeInMinutes() {
        return retentionTime/60.0;
    }

    public double[] getMassValues() {
        Iterator<Peak> it = peaks.iterator();
        double[] mass = new double[peaksCount() ];
        int count = 0;
        while (it.hasNext()) {
            Peak p = it.next();
            mass[count++] = p.getMz();
        }
        return mass;
    }

    public double[] getIntensityValues() {
        Iterator<Peak> it = peaks.iterator();
        double[] intensities = new double[peaksCount()];
        int count = 0;
        while (it.hasNext()) {
            Peak p = it.next();
            intensities[count++] = p.getIntensity();
        }
        return intensities;
    }

    protected void addPeak(Peak p) {
        if (p.spectrum != this) {
            throw new IllegalArgumentException("a peak cannot belong to more than one spectrum");
        }
        peaks.add(p);
    }

    public void addPeak(double mass, double intensity) {
        addPeak(new Peak(mass, intensity, this));
    }

    public List<Peak> getPeaks() {
        return peaks;
    }
    
    public int peaksCount() {
        return peaks.size();
    }

    public void setAnnotation(String key, Object value) {
        if (annotations == null) {
            annotations = new HashMap<String, Object>();
        }
        annotations.put(key, value);
    }

    public Object getAnnotation(String key) {
        if (annotations == null) {
            return null;
        }
        return annotations.get(key);
    }

    public Iterator<String> getAnnotations() {
        return annotations.keySet().iterator();
    }

}
