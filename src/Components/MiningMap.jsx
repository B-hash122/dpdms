import React, { useState, useEffect } from 'react';
import axios from 'axios';

export default function MiningMap() {
    const [incidents, setIncidents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(() => {
        fetchIncidents();
    }, []);

    const fetchIncidents = async () => {
        setLoading(true);
        setErrorMsg('');
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8086/api/v1/mining', {
                headers: { ...(token && { Authorization: `Bearer ${token}` }) }
            });

            console.log("Map Hazards Data Received:", response.data);

            // Handle standard array or Spring Boot Pageable response
            if (Array.isArray(response.data)) {
                setIncidents(response.data);
            } else if (response.data && Array.isArray(response.data.content)) {
                setIncidents(response.data.content);
            } else {
                setIncidents([]);
            }
        } catch (err) {
            console.error('Error fetching map points:', err);
            setErrorMsg('Could not retrieve spatial hazard coordinates from backend.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h1 style={styles.title}>Spatial Hazard Map</h1>
                <p style={styles.subtitle}>Interactive spatial coordinates recorded for active mining hazards.</p>
            </div>

            {errorMsg && (
                <div style={styles.alertError}>
                    {errorMsg}
                </div>
            )}

            <div style={styles.card}>
                <div style={styles.cardHeader}>
                    <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '600' }}>
                        Logged Coordinate Pins ({incidents.length})
                    </h3>
                </div>

                {loading ? (
                    <p style={styles.empty}>Loading spatial hazard data...</p>
                ) : incidents.length === 0 ? (
                    <p style={styles.empty}>No hazard coordinates currently logged on the map.</p>
                ) : (
                    <div style={styles.grid}>
                        {incidents.map((inc, index) => {
                            // Default coordinates if missing in submitted payload
                            const lat = inc.latitude || '-17.8252';
                            const lng = inc.longitude || '31.0335';
                            const mapsUrl = `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`;

                            return (
                                <div key={inc.id || index} style={styles.pinCard}>
                                    <div style={styles.pinHeader}>
                                        <h4 style={styles.pinTitle}>
                                            {inc.mineName || inc.title || 'Unnamed Mining Site'}
                                        </h4>
                                        <span style={styles.badge(inc.severityLevel)}>
                                            {inc.severityLevel || 'MEDIUM'}
                                        </span>
                                    </div>

                                    <p style={styles.metaLine}>
                                        <strong>Type:</strong> {inc.accidentType || 'Collapse'} ({inc.mineType || 'Artisanal'})
                                    </p>

                                    <p style={styles.metaLine}>
                                        <strong>Location:</strong> {inc.ward ? `Ward ${inc.ward}` : 'Ward N/A'}, {inc.district || 'Mutare'}
                                    </p>

                                    <div style={styles.coordBox}>
                                        <span>📍 Lat: {lat} | Long: {lng}</span>
                                        <a
                                            href={mapsUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            style={styles.mapLink}
                                        >
                                            View Map ↗
                                        </a>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
}

const styles = {
    container: { maxWidth: '1050px', margin: '0 auto', paddingBottom: '40px', color: '#0f172a', fontFamily: 'Inter, system-ui, sans-serif' },
    header: { marginBottom: '24px' },
    title: { fontSize: '24px', fontWeight: '700', margin: '0 0 4px 0' },
    subtitle: { fontSize: '14px', color: '#64748b', margin: 0 },
    card: { backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #e2e8f0', padding: '24px', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)' },
    cardHeader: { borderBottom: '1px solid #f1f5f9', paddingBottom: '12px', marginBottom: '16px' },
    empty: { textAlign: 'center', color: '#94a3b8', padding: '20px' },
    alertError: { padding: '12px 16px', backgroundColor: '#fee2e2', color: '#b91c1c', borderRadius: '8px', border: '1px solid #fecaca', marginBottom: '20px', fontSize: '14px' },
    grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '16px' },
    pinCard: { border: '1px solid #e2e8f0', borderRadius: '8px', padding: '16px', backgroundColor: '#f8fafc', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' },
    pinHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px', gap: '8px' },
    pinTitle: { margin: 0, fontSize: '15px', fontWeight: '700', color: '#1e293b' },
    metaLine: { margin: '3px 0', fontSize: '13px', color: '#475569' },
    coordBox: { marginTop: '12px', padding: '8px 10px', backgroundColor: '#e2e8f0', borderRadius: '6px', fontSize: '12px', fontWeight: '600', color: '#334155', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    mapLink: { color: '#2563eb', textDecoration: 'none', fontWeight: '700', fontSize: '11px' },
    badge: (lvl) => {
        const level = (lvl || '').toUpperCase();
        return {
            backgroundColor: level === 'CRITICAL' ? '#fee2e2' : level === 'HIGH' ? '#ffedd5' : level === 'LOW' ? '#f1f5f9' : '#fef3c7',
            color: level === 'CRITICAL' ? '#991b1b' : level === 'HIGH' ? '#9a3412' : level === 'LOW' ? '#334155' : '#92400e',
            fontSize: '11px', fontWeight: '700', padding: '2px 8px', borderRadius: '4px', whiteSpace: 'nowrap'
        };
    }
};