import React, { useState, useEffect } from 'react';
import axios from 'axios';

const getSeverityBadgeStyle = (level) => ({
    backgroundColor: level === 'CRITICAL' ? '#fee2e2' : level === 'HIGH' ? '#ffedd5' : '#f1f5f9',
    color: level === 'CRITICAL' ? '#991b1b' : level === 'HIGH' ? '#9a3412' : '#334155',
    fontSize: '11px',
    fontWeight: '700',
    padding: '2px 8px',
    borderRadius: '4px'
});

export default function IncidentDashboard() {
    const [formData, setFormData] = useState({
        title: '',
        mineName: '',
        mineType: 'Artisanal',
        accidentType: 'Collapse',
        description: '',
        severityLevel: 'LOW',
        rescueStatus: 'Ongoing',
        trappedOrInjuredCount: 0,
        fatalityCount: 0,
        ward: '',
        district: '',
        province: '',
        latitude: -17.8252,
        longitude: 31.0335,
        reportedBy: ''
    });

    const [incidents, setIncidents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);

    useEffect(() => {
        fetchIncidents();
    }, []);

    const fetchIncidents = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8086/api/v1/mining', {
                headers: { ...(token && { Authorization: `Bearer ${token}` }) }
            });
            setIncidents(response.data);
        } catch (err) {
            console.error('Failed to load incidents:', err);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: name.includes('Count') || name.includes('tude') ? (value === '' ? '' : parseFloat(value) || 0) : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);

        const payload = {
            ...formData,
            location: formData.mineName,
            status: 'PENDING',
            trappedOrInjuredCount: Number(formData.trappedOrInjuredCount),
            fatalityCount: Number(formData.fatalityCount),
            latitude: Number(formData.latitude),
            longitude: Number(formData.longitude)
        };

        try {
            const token = localStorage.getItem('token');
            const response = await axios.post('http://localhost:8086/api/v1/mining', payload, {
                headers: {
                    'Content-Type': 'application/json',
                    ...(token && { Authorization: `Bearer ${token}` })
                }
            });

            setMessage({ type: 'success', text: 'Incident successfully logged and submitted for review.' });
            setIncidents([response.data, ...incidents]);

            setFormData((prev) => ({
                ...prev,
                title: '',
                mineName: '',
                description: '',
                trappedOrInjuredCount: 0,
                fatalityCount: 0,
                reportedBy: ''
            }));
        } catch (err) {
            console.error('Submission error:', err);
            const serverErrMsg = err.response?.data?.message || err.message || 'Failed to submit incident to server.';
            setMessage({ type: 'error', text: `Submission Error: ${serverErrMsg}` });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.pageHeader}>
                <h1 style={styles.pageTitle}>Mining Incident Management</h1>
                <p style={styles.pageSubtitle}>
                    Ward Recorder Portal — Submit hazard logs and critical casualty indicators for provincial verification.
                </p>
            </div>

            {message && (
                <div style={message.type === 'success' ? styles.alertSuccess : styles.alertError}>
                    {message.text}
                </div>
            )}

            <div style={styles.card}>
                <div style={styles.cardHeader}>
                    <h2 style={styles.cardTitle}>Report New Mining Incident</h2>
                    <span style={styles.badgePending}>Status on Submit: PENDING</span>
                </div>

                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.sectionTitle}>1. Incident Identification & Overview</div>
                    <div style={styles.grid2}>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Incident Title *</label>
                            <input
                                type="text"
                                name="title"
                                required
                                value={formData.title}
                                onChange={handleChange}
                                placeholder="e.g. Shaft 4 Structural Collapse"
                                style={styles.input}
                            />
                        </div>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Location / Specific Site Name *</label>
                            <input
                                type="text"
                                name="mineName"
                                required
                                value={formData.mineName}
                                onChange={handleChange}
                                placeholder="e.g. Chiadzwa Pit 3"
                                style={styles.input}
                            />
                        </div>
                    </div>

                    <div style={styles.fieldGroup}>
                        <label style={styles.label}>Detailed Description</label>
                        <textarea
                            name="description"
                            rows="3"
                            value={formData.description}
                            onChange={handleChange}
                            placeholder="Provide contextual information, initial trigger, surrounding hazards..."
                            style={styles.textarea}
                        />
                    </div>

                    <div style={styles.sectionTitle}>2. Geographic & Spatial Metadata</div>
                    <div style={styles.grid3}>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Ward Name/Number *</label>
                            <input
                                type="text"
                                name="ward"
                                required
                                value={formData.ward}
                                onChange={handleChange}
                                placeholder="Ward 12"
                                style={styles.input}
                            />
                        </div>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>District *</label>
                            <input
                                type="text"
                                name="district"
                                required
                                value={formData.district}
                                onChange={handleChange}
                                placeholder="Mutare"
                                style={styles.input}
                            />
                        </div>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Province *</label>
                            <input
                                type="text"
                                name="province"
                                required
                                value={formData.province}
                                onChange={handleChange}
                                placeholder="Manicaland"
                                style={styles.input}
                            />
                        </div>
                    </div>

                    <div style={styles.grid2}>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Latitude Coordinate (GPS)</label>
                            <input
                                type="number"
                                step="any"
                                name="latitude"
                                value={formData.latitude}
                                onChange={handleChange}
                                style={styles.input}
                            />
                        </div>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Longitude Coordinate (GPS)</label>
                            <input
                                type="number"
                                step="any"
                                name="longitude"
                                value={formData.longitude}
                                onChange={handleChange}
                                style={styles.input}
                            />
                        </div>
                    </div>

                    <div style={styles.sectionTitle}>3. Critical Hazard & Emergency Metrics</div>
                    <div style={styles.grid3}>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Mine Type</label>
                            <select name="mineType" value={formData.mineType} onChange={handleChange} style={styles.select}>
                                <option value="Artisanal">Artisanal / Small-Scale</option>
                                <option value="Formal">Formal Commercial</option>
                                <option value="Illegal">Unlicensed / Illegal</option>
                                <option value="Abandoned">Abandoned Operation</option>
                            </select>
                        </div>

                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Accident Classification</label>
                            <select name="accidentType" value={formData.accidentType} onChange={handleChange} style={styles.select}>
                                <option value="Collapse">Shaft / Pit Collapse</option>
                                <option value="Flooding">Tunnel Flooding</option>
                                <option value="Gas Explosion">Gas Leak / Explosion</option>
                                <option value="Equipment Failure">Heavy Machinery Accident</option>
                                <option value="Chemical Exposure">Chemical Exposure</option>
                            </select>
                        </div>

                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Trapped / Injured Count</label>
                            <input
                                type="number"
                                min="0"
                                name="trappedOrInjuredCount"
                                value={formData.trappedOrInjuredCount}
                                onChange={handleChange}
                                style={styles.input}
                            />
                        </div>
                    </div>

                    <div style={styles.grid3}>
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Fatality Count</label>
                            <input
                                type="number"
                                min="0"
                                name="fatalityCount"
                                value={formData.fatalityCount}
                                onChange={handleChange}
                                style={{ ...styles.input, borderColor: formData.fatalityCount > 0 ? '#ef4444' : '#cbd5e1' }}
                            />
                        </div>

                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Rescue Operation Status</label>
                            <select name="rescueStatus" value={formData.rescueStatus} onChange={handleChange} style={styles.select}>
                                <option value="Ongoing">Active / Ongoing</option>
                                <option value="Pending">Awaiting Rescue Teams</option>
                                <option value="Completed">Completed / Stated Safe</option>
                                <option value="Suspended">Operation Suspended</option>
                            </select>
                        </div>

                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Assessed Severity</label>
                            <select name="severityLevel" value={formData.severityLevel} onChange={handleChange} style={styles.select}>
                                <option value="LOW">Low Risk</option>
                                <option value="MEDIUM">Medium Emergency</option>
                                <option value="HIGH">High Severity</option>
                                <option value="CRITICAL">Critical Emergency</option>
                            </select>
                        </div>
                    </div>

                    <div style={styles.fieldGroup}>
                        <label style={styles.label}>Reported By (Officer Name / ID) *</label>
                        <input
                            type="text"
                            name="reportedBy"
                            required
                            value={formData.reportedBy}
                            onChange={handleChange}
                            placeholder="Officer Name or Ward ID"
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.actionRow}>
                        <button type="submit" disabled={loading} style={styles.submitBtn}>
                            {loading ? 'Submitting Record...' : 'Submit Incident Entry'}
                        </button>
                    </div>
                </form>
            </div>

            <div style={{ ...styles.card, marginTop: '24px' }}>
                <div style={styles.cardHeader}>
                    <h2 style={styles.cardTitle}>Recent Ward Incident Submissions</h2>
                </div>

                {incidents.length === 0 ? (
                    <p style={styles.emptyText}>No recent incidents logged.</p>
                ) : (
                    <div style={{ overflowX: 'auto' }}>
                        <table style={styles.table}>
                            <thead>
                            <tr>
                                <th style={styles.th}>ID</th>
                                <th style={styles.th}>Mine / Title</th>
                                <th style={styles.th}>Location</th>
                                <th style={styles.th}>Casualties</th>
                                <th style={styles.th}>Severity</th>
                                <th style={styles.th}>Approval State</th>
                            </tr>
                            </thead>
                            <tbody>
                            {incidents.map((inc) => (
                                <tr key={inc.id || Math.random()} style={styles.tr}>
                                    <td style={styles.td}>#{inc.id || 'NEW'}</td>
                                    <td style={styles.td}>
                                        <strong>{inc.mineName || inc.title}</strong>
                                        <div style={styles.subtext}>{inc.accidentType} • {inc.mineType}</div>
                                    </td>
                                    <td style={styles.td}>
                                        Ward {inc.ward}, {inc.district}
                                        <div style={styles.subtext}>{inc.latitude}, {inc.longitude}</div>
                                    </td>
                                    <td style={styles.td}>
                                            <span style={{ color: inc.trappedOrInjuredCount > 0 ? '#d97706' : '#334155' }}>
                                                Injured: {inc.trappedOrInjuredCount || 0}
                                            </span>
                                        <br />
                                        <span style={{ color: inc.fatalityCount > 0 ? '#dc2626' : '#334155', fontWeight: inc.fatalityCount > 0 ? '700' : '400' }}>
                                                Fatal: {inc.fatalityCount || 0}
                                            </span>
                                    </td>
                                    <td style={styles.td}>
                                        <span style={getSeverityBadgeStyle(inc.severityLevel)}>{inc.severityLevel}</span>
                                    </td>
                                    <td style={styles.td}>
                                        <span style={styles.badgePending}>{inc.status || 'PENDING'}</span>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}

const styles = {
    container: { maxWidth: '1050px', margin: '0 auto', paddingBottom: '40px', color: '#0f172a', fontFamily: 'Inter, system-ui, sans-serif' },
    pageHeader: { marginBottom: '24px' },
    pageTitle: { fontSize: '24px', fontWeight: '700', color: '#0f172a', margin: '0 0 6px 0' },
    pageSubtitle: { fontSize: '14px', color: '#64748b', margin: 0 },
    card: { backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)', padding: '24px', marginBottom: '20px' },
    cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingBottom: '16px', marginBottom: '20px', borderBottom: '1px solid #f1f5f9' },
    cardTitle: { fontSize: '18px', fontWeight: '700', color: '#0f172a', margin: 0 },
    sectionTitle: { fontSize: '12px', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em', color: '#2563eb', marginTop: '16px', marginBottom: '12px' },
    form: { display: 'flex', flexDirection: 'column', gap: '12px' },
    grid2: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' },
    grid3: { display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' },
    fieldGroup: { display: 'flex', flexDirection: 'column', gap: '6px' },
    label: { fontSize: '13px', fontWeight: '600', color: '#334155' },
    input: { padding: '10px 12px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', outline: 'none' },
    textarea: { padding: '10px 12px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', fontFamily: 'inherit', outline: 'none' },
    select: { padding: '10px 12px', borderRadius: '6px', border: '1px solid #cbd5e1', backgroundColor: '#ffffff', fontSize: '14px', outline: 'none' },
    actionRow: { display: 'flex', justifyContent: 'flex-end', marginTop: '16px' },
    submitBtn: { backgroundColor: '#2563eb', color: '#ffffff', border: 'none', padding: '12px 24px', fontSize: '14px', fontWeight: '600', borderRadius: '6px', cursor: 'pointer' },
    alertSuccess: { padding: '12px 16px', backgroundColor: '#dcfce7', color: '#15803d', borderRadius: '8px', border: '1px solid #bbf7d0', marginBottom: '20px', fontSize: '14px', fontWeight: '500' },
    alertError: { padding: '12px 16px', backgroundColor: '#fee2e2', color: '#b91c1c', borderRadius: '8px', border: '1px solid #fecaca', marginBottom: '20px', fontSize: '14px', fontWeight: '500' },
    badgePending: { backgroundColor: '#fef3c7', color: '#92400e', fontSize: '12px', fontWeight: '700', padding: '4px 10px', borderRadius: '9999px' },
    table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left' },
    th: { padding: '12px', borderBottom: '2px solid #e2e8f0', fontSize: '12px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase' },
    tr: { borderBottom: '1px solid #f1f5f9' },
    td: { padding: '12px', fontSize: '14px', color: '#1e293b' },
    subtext: { fontSize: '12px', color: '#64748b', marginTop: '2px' },
    emptyText: { textAlign: 'center', color: '#94a3b8', padding: '20px 0' }
};