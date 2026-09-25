import React, { useState } from 'react';
import IncidentDashboard from './Components/IncidentDashboard';
import SupervisorApprovalQueue from './Components/SupervisorApprovalQueue';
import MiningMap from './Components/MiningMap';

export default function App() {
    const [activeTab, setActiveTab] = useState('recorder');

    return (
        <div style={{ backgroundColor: '#f8fafc', minHeight: '100vh' }}>
            {/* Top Navbar */}
            <nav style={styles.navbar}>
                <div style={styles.navBrand}>
                    <strong>DPDMS</strong> <span style={{ opacity: 0.8, fontWeight: 400 }}>MINING HAZARD PORTAL</span>
                </div>
                <div style={styles.navTabs}>
                    <button
                        onClick={() => setActiveTab('recorder')}
                        style={activeTab === 'recorder' ? styles.activeTabBtn : styles.tabBtn}
                    >
                        Ward Recorder Dashboard
                    </button>
                    <button
                        onClick={() => setActiveTab('supervisor')}
                        style={activeTab === 'supervisor' ? styles.activeTabBtn : styles.tabBtn}
                    >
                        Supervisor Queue
                    </button>
                    <button
                        onClick={() => setActiveTab('hazard-map')}
                        style={activeTab === 'hazard-map' ? styles.activeTabBtn : styles.tabBtn}
                    >
                        Hazard Map
                    </button>
                </div>
            </nav>

            {/* Main Content Area */}
            <main style={{ padding: '24px 16px' }}>
                {activeTab === 'recorder' && <IncidentDashboard />}
                {activeTab === 'supervisor' && <SupervisorApprovalQueue />}
                {activeTab === 'hazard-map' && <MiningMap />}
            </main>
        </div>
    );
}

const styles = {
    navbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#0f172a', padding: '12px 32px', color: '#fff' },
    navBrand: { fontSize: '16px', letterSpacing: '0.5px' },
    navTabs: { display: 'flex', gap: '8px' },
    tabBtn: { backgroundColor: 'transparent', color: '#cbd5e1', border: 'none', padding: '8px 16px', borderRadius: '6px', cursor: 'pointer', fontSize: '14px' },
    activeTabBtn: { backgroundColor: '#2563eb', color: '#ffffff', border: 'none', padding: '8px 16px', borderRadius: '6px', fontWeight: '600', fontSize: '14px', cursor: 'pointer' }
};