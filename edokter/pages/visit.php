<?php
// Modern CPPT/SOAP page for doctors (web).

if (!isset($_SESSION["ses_admin"])) {
    JSRedirect("index.php?act=Home");
    exit;
}

function dv_h($s) {
    return htmlspecialchars((string)$s, ENT_QUOTES, "UTF-8");
}

function dv_pill_class($stts) {
    $stts = strtoupper(trim((string)$stts));
    if ($stts === "SUDAH") return "dv-pill dv-pill--done";
    if ($stts === "BATAL") return "dv-pill dv-pill--cancel";
    return "dv-pill dv-pill--todo";
}

$kd_dokter = validTeks4(encrypt_decrypt($_SESSION["ses_admin"], "d"), 20);
$no_rawat = isset($_GET["no_rawat"]) ? validTeks4($_GET["no_rawat"], 30) : "";

$flash_ok = "";
$flash_err = "";

// Handle actions.
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $dv_action = isset($_POST["dv_action"]) ? validTeks4($_POST["dv_action"], 20) : "";
    $posted_no_rawat = isset($_POST["no_rawat"]) ? validTeks4($_POST["no_rawat"], 30) : "";

    if ($posted_no_rawat !== "") {
        $no_rawat = $posted_no_rawat;
    }

    // Ensure the visit belongs to the logged-in doctor and is for today.
    $cek = getOne("select count(*) from reg_periksa where no_rawat='" . cleankar2($no_rawat) . "' and kd_dokter='" . cleankar2($kd_dokter) . "' and tgl_registrasi=current_date()");
    if ((int)$cek <= 0) {
        $flash_err = "No.Rawat tidak valid, atau bukan pasien Anda hari ini.";
    } else {
        if ($dv_action === "save_cppt") {
            $suhu_tubuh = cleankar2(isset($_POST["suhu_tubuh"]) ? trim($_POST["suhu_tubuh"]) : "");
            $tensi = cleankar2(isset($_POST["tensi"]) ? trim($_POST["tensi"]) : "");
            $nadi = cleankar2(isset($_POST["nadi"]) ? trim($_POST["nadi"]) : "");
            $respirasi = cleankar2(isset($_POST["respirasi"]) ? trim($_POST["respirasi"]) : "");
            $tinggi = cleankar2(isset($_POST["tinggi"]) ? trim($_POST["tinggi"]) : "");
            $berat = cleankar2(isset($_POST["berat"]) ? trim($_POST["berat"]) : "");
            $spo2 = cleankar2(isset($_POST["spo2"]) ? trim($_POST["spo2"]) : "");
            $gcs = cleankar2(isset($_POST["gcs"]) ? trim($_POST["gcs"]) : "");
            $kesadaran = cleankar2(isset($_POST["kesadaran"]) ? trim($_POST["kesadaran"]) : "");

            $keluhan = cleankar2(isset($_POST["keluhan"]) ? trim($_POST["keluhan"]) : "");
            $pemeriksaan = cleankar2(isset($_POST["pemeriksaan"]) ? trim($_POST["pemeriksaan"]) : "");
            $alergi = cleankar2(isset($_POST["alergi"]) ? trim($_POST["alergi"]) : "");
            $lingkar_perut = cleankar2(isset($_POST["lingkar_perut"]) ? trim($_POST["lingkar_perut"]) : "");

            $rtl = cleankar2(isset($_POST["rtl"]) ? trim($_POST["rtl"]) : "");
            $penilaian = cleankar2(isset($_POST["penilaian"]) ? trim($_POST["penilaian"]) : "");
            $instruksi = cleankar2(isset($_POST["instruksi"]) ? trim($_POST["instruksi"]) : "");
            $evaluasi = cleankar2(isset($_POST["evaluasi"]) ? trim($_POST["evaluasi"]) : "");

            $tgl = date("Y-m-d");
            $jam = date("H:i:s");
            $nip = cleankar2($kd_dokter);

            // Table shape follows DlgRawatJalan: 21 fields.
            $sql = "insert into pemeriksaan_ralan values (" .
                "'" . cleankar2($no_rawat) . "'," .
                "'" . $tgl . "'," .
                "'" . $jam . "'," .
                "'" . $suhu_tubuh . "'," .
                "'" . $tensi . "'," .
                "'" . $nadi . "'," .
                "'" . $respirasi . "'," .
                "'" . $tinggi . "'," .
                "'" . $berat . "'," .
                "'" . $spo2 . "'," .
                "'" . $gcs . "'," .
                "'" . $kesadaran . "'," .
                "'" . $keluhan . "'," .
                "'" . $pemeriksaan . "'," .
                "'" . $alergi . "'," .
                "'" . $lingkar_perut . "'," .
                "'" . $rtl . "'," .
                "'" . $penilaian . "'," .
                "'" . $instruksi . "'," .
                "'" . $evaluasi . "'," .
                "'" . $nip . "'" .
            ")";

            $ok = bukaquery($sql);
            if ($ok) {
                $flash_ok = "CPPT/SOAP tersimpan (" . dv_h($tgl) . " " . dv_h($jam) . ").";
                if (isset($_POST["tandai_selesai"]) && $_POST["tandai_selesai"] === "1") {
                    bukaquery("update reg_periksa set stts='Sudah' where no_rawat='" . cleankar2($no_rawat) . "' and kd_dokter='" . cleankar2($kd_dokter) . "' and tgl_registrasi=current_date()");
                }
            } else {
                $flash_err = "Gagal menyimpan CPPT/SOAP. Pastikan tabel pemeriksaan_ralan ada dan struktur kolom sesuai.";
            }
        } elseif ($dv_action === "mark_done") {
            $ok = bukaquery("update reg_periksa set stts='Sudah' where no_rawat='" . cleankar2($no_rawat) . "' and kd_dokter='" . cleankar2($kd_dokter) . "' and tgl_registrasi=current_date()");
            if ($ok) $flash_ok = "Status pasien diubah menjadi Sudah.";
            else $flash_err = "Gagal mengubah status pasien.";
        }
    }
}

// Queue for today.
$queryQueue = bukaquery(
    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk," .
    "concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur) as umur,reg_periksa.stts,poliklinik.nm_poli " .
    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " .
    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli " .
    "where reg_periksa.kd_dokter='" . cleankar2($kd_dokter) . "' and reg_periksa.tgl_registrasi=current_date() " .
    "order by reg_periksa.no_reg asc"
);

$visit = null;
$notes = null;
if ($no_rawat !== "") {
    $q = bukaquery(
        "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.no_rkm_medis," .
        "pasien.nm_pasien,pasien.jk,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur) as umur,reg_periksa.stts," .
        "poliklinik.nm_poli,penjab.png_jawab " .
        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " .
        "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli " .
        "left join penjab on reg_periksa.kd_pj=penjab.kd_pj " .
        "where reg_periksa.no_rawat='" . cleankar2($no_rawat) . "' and reg_periksa.kd_dokter='" . cleankar2($kd_dokter) . "' and reg_periksa.tgl_registrasi=current_date() " .
        "limit 1"
    );
    $visit = mysqli_fetch_assoc($q);

    if ($visit) {
        $notes = bukaquery(
            "select tgl_perawatan,jam_rawat,keluhan,pemeriksaan,penilaian,rtl,instruksi,evaluasi,alergi," .
            "suhu_tubuh,tensi,nadi,respirasi,tinggi,berat,spo2,gcs,kesadaran,lingkar_perut,nip " .
            "from pemeriksaan_ralan where no_rawat='" . cleankar2($no_rawat) . "' " .
            "order by tgl_perawatan desc, jam_rawat desc limit 10"
        );
    }
}

?>

<div class="doc-visit">
    <div class="dv-hero">
        <div class="dv-hero__title">CPPT/SOAP Rawat Jalan</div>
        <div class="dv-hero__meta">
            Dokter: <b><?=dv_h($_SESSION["nm_dokter"]);?></b>
            <span style="margin-left: 8px;">Tanggal: <b><?=dv_h(date("Y-m-d"));?></b></span>
        </div>
    </div>

    <?php if ($flash_ok !== "") { ?>
        <div class="dv-flash"><?=dv_h($flash_ok);?></div>
    <?php } ?>
    <?php if ($flash_err !== "") { ?>
        <div class="dv-flash dv-flash--error"><?=dv_h($flash_err);?></div>
    <?php } ?>

    <div class="dv-grid">
        <div class="dv-panel">
            <div class="dv-panel__head">
                <h3>Antrian Anda Hari Ini</h3>
            </div>
            <div class="dv-section">
                <input class="dv-input" type="text" placeholder="Cari pasien / No.Rawat / No.RM / Poli" data-dv-queue-search>
            </div>
            <div class="dv-queue">
                <?php
                $ada = false;
                while ($row = mysqli_fetch_assoc($queryQueue)) {
                    $ada = true;
                    $active = ($no_rawat !== "" && $row["no_rawat"] === $no_rawat) ? " dv-queue__item--active" : "";
                    $hay = $row["nm_pasien"] . " " . $row["no_rawat"] . " " . $row["no_rkm_medis"] . " " . $row["nm_poli"] . " " . $row["stts"];
                ?>
                    <a class="dv-queue__item<?=$active;?>" data-dv-queue-item data-dv-hay="<?=dv_h($hay);?>" href="index.php?act=Visit&no_rawat=<?=dv_h(urlencode($row["no_rawat"]));?>">
                        <div class="dv-queue__primary">
                            <div class="dv-queue__name"><?=dv_h($row["nm_pasien"]);?></div>
                            <div class="dv-queue__sub">
                                <?=dv_h($row["no_rawat"]);?> · RM <?=dv_h($row["no_rkm_medis"]);?> · <?=dv_h($row["nm_poli"]);?>
                            </div>
                        </div>
                        <div class="<?=dv_pill_class($row["stts"]);?>"><?=dv_h($row["stts"]);?></div>
                    </a>
                <?php } ?>
                <?php if (!$ada) { ?>
                    <div class="dv-section" style="color: var(--dv-muted); font-size: 13px;">
                        Belum ada pasien rawat jalan untuk Anda hari ini.
                    </div>
                <?php } ?>
            </div>
        </div>

        <div class="dv-panel">
            <div class="dv-panel__head">
                <h3>Ringkas Pasien</h3>
                <div class="dv-actions">
                    <a class="dv-btn" href="index.php?act=Pasien">Daftar Pasien</a>
                </div>
            </div>

            <?php if (!$visit) { ?>
                <div class="dv-section" style="color: var(--dv-muted);">
                    Pilih pasien dari panel kiri untuk mulai isi CPPT/SOAP.
                </div>
            <?php } else { ?>
                <div class="dv-section">
                    <div class="dv-kv">
                        <div class="dv-kv__k">Nama</div>
                        <div><b><?=dv_h($visit["nm_pasien"]);?></b> (<?=dv_h($visit["jk"]);?>, <?=dv_h($visit["umur"]);?>)</div>

                        <div class="dv-kv__k">No.Rawat</div>
                        <div><b><?=dv_h($visit["no_rawat"]);?></b> · No.Reg <?=dv_h($visit["no_reg"]);?></div>

                        <div class="dv-kv__k">No.RM</div>
                        <div><?=dv_h($visit["no_rkm_medis"]);?></div>

                        <div class="dv-kv__k">Poli</div>
                        <div><?=dv_h($visit["nm_poli"]);?></div>

                        <div class="dv-kv__k">Penjamin</div>
                        <div><?=dv_h($visit["png_jawab"]);?></div>

                        <div class="dv-kv__k">Status</div>
                        <div><span class="<?=dv_pill_class($visit["stts"]);?>"><?=dv_h($visit["stts"]);?></span></div>
                    </div>

                    <div class="dv-divider"></div>

                    <div class="dv-actions">
                        <form method="post" style="margin:0; display:inline;">
                            <input type="hidden" name="dv_action" value="mark_done">
                            <input type="hidden" name="no_rawat" value="<?=dv_h($visit["no_rawat"]);?>">
                            <button class="dv-btn dv-btn--primary" type="submit">Tandai Sudah</button>
                        </form>
                        <a class="dv-btn" href="index.php?act=Resep&no_rawat=<?=dv_h(urlencode($visit["no_rawat"]));?>">Resep</a>
                        <a class="dv-btn" href="index.php?act=Visit">Bersihkan Pilihan</a>
                    </div>
                </div>

                <div class="dv-panel__head">
                    <h3>Isi CPPT/SOAP</h3>
                </div>

                <div class="dv-section">
                    <form method="post" autocomplete="off">
                        <input type="hidden" name="dv_action" value="save_cppt">
                        <input type="hidden" name="no_rawat" value="<?=dv_h($visit["no_rawat"]);?>">

                        <div class="dv-form-grid">
                            <div class="dv-field">
                                <label>Suhu</label>
                                <input class="dv-input" name="suhu_tubuh" placeholder="36.8" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>Tensi</label>
                                <input class="dv-input" name="tensi" placeholder="120/80" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>Nadi</label>
                                <input class="dv-input" name="nadi" placeholder="80" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>RR</label>
                                <input class="dv-input" name="respirasi" placeholder="20" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>TB</label>
                                <input class="dv-input" name="tinggi" placeholder="170" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>BB</label>
                                <input class="dv-input" name="berat" placeholder="65" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>SpO2</label>
                                <input class="dv-input" name="spo2" placeholder="98" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>GCS</label>
                                <input class="dv-input" name="gcs" placeholder="15" maxlength="10">
                            </div>
                            <div class="dv-field">
                                <label>Kesadaran</label>
                                <input class="dv-input" name="kesadaran" placeholder="Compos mentis" maxlength="50">
                            </div>
                            <div class="dv-field">
                                <label>Lingkar Perut</label>
                                <input class="dv-input" name="lingkar_perut" placeholder="cm" maxlength="10">
                            </div>
                            <div class="dv-field" style="grid-column: span 2;">
                                <label>Alergi</label>
                                <input class="dv-input" name="alergi" placeholder="Contoh: amoksisilin" maxlength="200">
                            </div>
                        </div>

                        <div class="dv-divider"></div>

                        <div class="dv-field">
                            <label>Keluhan (S)</label>
                            <textarea class="dv-input dv-textarea" name="keluhan" maxlength="2000" placeholder="Keluhan utama, riwayat singkat..."></textarea>
                        </div>

                        <div class="dv-field" style="margin-top: 10px;">
                            <label>Pemeriksaan (O)</label>
                            <textarea class="dv-input dv-textarea" name="pemeriksaan" maxlength="2000" placeholder="Temuan pemeriksaan fisik, penunjang..."></textarea>
                        </div>

                        <div class="dv-field" style="margin-top: 10px;">
                            <label>Penilaian (A)</label>
                            <textarea class="dv-input dv-textarea" name="penilaian" maxlength="2000" placeholder="Diagnosa kerja/banding..."></textarea>
                        </div>

                        <div class="dv-field" style="margin-top: 10px;">
                            <label>Rencana Tindak Lanjut (P)</label>
                            <textarea class="dv-input dv-textarea" name="rtl" maxlength="2000" placeholder="Terapi, edukasi, kontrol, rujuk..."></textarea>
                        </div>

                        <div class="dv-field" style="margin-top: 10px;">
                            <label>Instruksi</label>
                            <textarea class="dv-input dv-textarea" name="instruksi" maxlength="2000" placeholder="Instruksi untuk pasien/tenaga kesehatan..."></textarea>
                        </div>

                        <div class="dv-field" style="margin-top: 10px;">
                            <label>Evaluasi</label>
                            <textarea class="dv-input dv-textarea" name="evaluasi" maxlength="2000" placeholder="Evaluasi, respon terapi, rencana lanjut..."></textarea>
                        </div>

                        <div class="dv-actions" style="margin-top: 12px;">
                            <button class="dv-btn dv-btn--primary" type="submit">Simpan CPPT/SOAP</button>
                            <label style="display:flex; align-items:center; gap:8px; margin:0; font-size: 12px; color: var(--dv-muted);">
                                <input type="checkbox" name="tandai_selesai" value="1">
                                Tandai status pasien menjadi Sudah setelah simpan
                            </label>
                        </div>
                    </form>
                </div>

                <div class="dv-panel__head">
                    <h3>Riwayat CPPT/SOAP (Terakhir)</h3>
                </div>
                <div class="dv-section">
                    <?php
                    $adaNotes = false;
                    if ($notes) {
                        while ($n = mysqli_fetch_assoc($notes)) {
                            $adaNotes = true;
                    ?>
                        <div class="dv-note" style="margin-bottom: 10px;">
                            <div class="dv-note__head">
                                <div><b><?=dv_h($n["tgl_perawatan"]);?></b> <?=dv_h($n["jam_rawat"]);?></div>
                                <div>TTD: <?=dv_h($n["nip"]);?></div>
                            </div>
                            <div class="dv-note__body">
                                <?php if (trim($n["keluhan"]) !== "") { ?>
                                    <div><b>S:</b> <?=nl2br(dv_h($n["keluhan"]));?></div>
                                <?php } ?>
                                <?php if (trim($n["pemeriksaan"]) !== "") { ?>
                                    <div style="margin-top:6px;"><b>O:</b> <?=nl2br(dv_h($n["pemeriksaan"]));?></div>
                                <?php } ?>
                                <?php if (trim($n["penilaian"]) !== "") { ?>
                                    <div style="margin-top:6px;"><b>A:</b> <?=nl2br(dv_h($n["penilaian"]));?></div>
                                <?php } ?>
                                <?php if (trim($n["rtl"]) !== "") { ?>
                                    <div style="margin-top:6px;"><b>P:</b> <?=nl2br(dv_h($n["rtl"]));?></div>
                                <?php } ?>
                                <?php if (trim($n["instruksi"]) !== "") { ?>
                                    <div style="margin-top:6px;"><b>Instruksi:</b> <?=nl2br(dv_h($n["instruksi"]));?></div>
                                <?php } ?>
                                <?php if (trim($n["evaluasi"]) !== "") { ?>
                                    <div style="margin-top:6px;"><b>Evaluasi:</b> <?=nl2br(dv_h($n["evaluasi"]));?></div>
                                <?php } ?>
                                <div style="margin-top:8px; color: var(--dv-muted); font-size: 12px;">
                                    Vitals: Suhu <?=dv_h($n["suhu_tubuh"]);?> · Tensi <?=dv_h($n["tensi"]);?> · Nadi <?=dv_h($n["nadi"]);?> · RR <?=dv_h($n["respirasi"]);?> · SpO2 <?=dv_h($n["spo2"]);?>
                                </div>
                            </div>
                        </div>
                    <?php
                        }
                    }
                    ?>
                    <?php if (!$adaNotes) { ?>
                        <div style="color: var(--dv-muted); font-size: 13px;">Belum ada catatan CPPT/SOAP untuk No.Rawat ini.</div>
                    <?php } ?>
                </div>
            <?php } ?>
        </div>
    </div>
</div>
