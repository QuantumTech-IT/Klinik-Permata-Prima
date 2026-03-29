<?php
// Web version of "Peresepan Dokter" (simplified: Non Racikan) based on DlgPeresepanDokter.java.

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

function rx_summary_text($no_resep) {
    $no_resep = cleankar2($no_resep);
    $txt = "Resep :\n";
    $q = bukaquery(
        "select databarang.nama_brng,resep_dokter.jml,resep_dokter.aturan_pakai " .
        "from databarang inner join resep_dokter on databarang.kode_brng=resep_dokter.kode_brng " .
        "where resep_dokter.no_resep='" . $no_resep . "'"
    );
    while ($r = mysqli_fetch_assoc($q)) {
        $txt .= $r["nama_brng"] . " Jumlah " . $r["jml"] . " Aturan Pakai " . $r["aturan_pakai"] . "\n";
    }
    return $txt;
}

function rx_next_no_resep($tglYmd) {
    $prefix = cleankar2($tglYmd);
    $max = getOne("select ifnull(MAX(CAST(RIGHT(resep_obat.no_resep,4) AS UNSIGNED)),0) " .
                  "from resep_obat where (resep_obat.tgl_peresepan=current_date() or resep_obat.tgl_perawatan=current_date()) " .
                  "and resep_obat.no_resep like '" . $prefix . "%'");
    $next = ((int)$max) + 1;
    return $prefix . str_pad((string)$next, 4, "0", STR_PAD_LEFT);
}

$kd_dokter = validTeks4(encrypt_decrypt($_SESSION["ses_admin"], "d"), 20);
$no_rawat = isset($_GET["no_rawat"]) ? validTeks4($_GET["no_rawat"], 30) : "";

// AJAX: drug search.
if (isset($_GET["ajax"]) && $_GET["ajax"] === "obat") {
    header("Content-Type: application/json; charset=utf-8");
    $q = isset($_GET["q"]) ? trim($_GET["q"]) : "";
    $q = validTeks4($q, 60);
    if (strlen($q) < 2) {
        echo "[]";
        exit;
    }

    $like = "%" . cleankar2($q) . "%";
    $rs = bukaquery(
        "select databarang.kode_brng,databarang.nama_brng,databarang.kode_sat " .
        "from databarang " .
        "where databarang.kode_brng like '" . $like . "' or databarang.nama_brng like '" . $like . "' " .
        "order by databarang.nama_brng limit 20"
    );
    $out = array();
    while ($row = mysqli_fetch_assoc($rs)) {
        $out[] = array(
            "kode" => $row["kode_brng"],
            "nama" => $row["nama_brng"],
            "sat"  => $row["kode_sat"],
        );
    }
    echo json_encode($out);
    exit;
}

$flash_ok = "";
$flash_err = "";

// Handle actions.
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $rx_action = isset($_POST["rx_action"]) ? validTeks4($_POST["rx_action"], 20) : "";
    $posted_no_rawat = isset($_POST["no_rawat"]) ? validTeks4($_POST["no_rawat"], 30) : "";
    if ($posted_no_rawat !== "") $no_rawat = $posted_no_rawat;

    $cek = getOne("select count(*) from reg_periksa where no_rawat='" . cleankar2($no_rawat) . "' and kd_dokter='" . cleankar2($kd_dokter) . "' and tgl_registrasi=current_date()");
    if ((int)$cek <= 0) {
        $flash_err = "No.Rawat tidak valid, atau bukan pasien Anda hari ini.";
    } else {
        if ($rx_action === "save_resep") {
            $kode_brng = isset($_POST["kode_brng"]) && is_array($_POST["kode_brng"]) ? $_POST["kode_brng"] : array();
            $jml = isset($_POST["jml"]) && is_array($_POST["jml"]) ? $_POST["jml"] : array();
            $aturan = isset($_POST["aturan_pakai"]) && is_array($_POST["aturan_pakai"]) ? $_POST["aturan_pakai"] : array();

            $items = array();
            $cnt = min(count($kode_brng), count($jml), count($aturan));
            for ($i = 0; $i < $cnt; $i++) {
                $k = validTeks4(trim((string)$kode_brng[$i]), 20);
                $a = validTeks4(trim((string)$aturan[$i]), 200);
                $qty = trim((string)$jml[$i]);
                if ($k === "") continue;
                if (!is_numeric($qty) || (float)$qty <= 0) continue;
                $items[] = array(
                    "kode" => cleankar2($k),
                    "qty"  => (string)((float)$qty),
                    "aturan" => cleankar2($a),
                );
            }

            if (count($items) <= 0) {
                $flash_err = "Item resep kosong. Tambahkan minimal 1 obat dengan jumlah > 0.";
            } else {
                $tglYmd = date("Ymd");
                $tgl = date("Y-m-d");
                $jam = date("H:i:s");
                $status = "ralan";

                // Try insert header; retry if collision.
                $no_resep = "";
                $okHeader = false;
                for ($try = 0; $try < 5; $try++) {
                    $no_resep = rx_next_no_resep($tglYmd);
                    $sqlH = "insert into resep_obat values(" .
                        "'" . cleankar2($no_resep) . "'," .
                        "'0000-00-00'," .
                        "'00:00:00'," .
                        "'" . cleankar2($no_rawat) . "'," .
                        "'" . cleankar2($kd_dokter) . "'," .
                        "'" . $tgl . "'," .
                        "'" . $jam . "'," .
                        "'" . cleankar2($status) . "'," .
                        "'0000-00-00'," .
                        "'00:00:00'" .
                    ")";
                    $okHeader = bukaquery($sqlH);
                    if ($okHeader) break;
                }

                if (!$okHeader) {
                    $flash_err = "Gagal membuat header resep (resep_obat). Cek struktur tabel resep_obat / hak akses DB.";
                } else {
                    $okAll = true;
                    foreach ($items as $it) {
                        $sqlD = "insert into resep_dokter values(" .
                            "'" . cleankar2($no_resep) . "'," .
                            "'" . $it["kode"] . "'," .
                            "'" . cleankar2($it["qty"]) . "'," .
                            "'" . $it["aturan"] . "'" .
                        ")";
                        if (!bukaquery($sqlD)) {
                            $okAll = false;
                            break;
                        }
                    }

                    if ($okAll) {
                        if (isset($_POST["append_rtl"]) && $_POST["append_rtl"] === "1") {
                            $qLast = bukaquery("select tgl_perawatan,jam_rawat from pemeriksaan_ralan where no_rawat='" . cleankar2($no_rawat) . "' and nip='" . cleankar2($kd_dokter) . "' order by tgl_perawatan desc, jam_rawat desc limit 1");
                            $last = mysqli_fetch_assoc($qLast);
                            if ($last) {
                                $txt = rx_summary_text($no_resep);
                                bukaquery(
                                    "update pemeriksaan_ralan set rtl=concat(rtl,' " . cleankar2($txt) . "') " .
                                    "where no_rawat='" . cleankar2($no_rawat) . "' and tgl_perawatan='" . cleankar2($last["tgl_perawatan"]) . "' and jam_rawat='" . cleankar2($last["jam_rawat"]) . "' and nip='" . cleankar2($kd_dokter) . "'"
                                );
                            }
                        }
                        $flash_ok = "Resep tersimpan. No.Resep: " . dv_h($no_resep);
                    } else {
                        // Best-effort cleanup.
                        bukaquery("delete from resep_dokter where no_resep='" . cleankar2($no_resep) . "'");
                        bukaquery("delete from resep_obat where no_resep='" . cleankar2($no_resep) . "'");
                        $flash_err = "Gagal menyimpan detail resep. Transaksi dibatalkan.";
                    }
                }
            }
        } elseif ($rx_action === "delete_resep") {
            $no_resep = isset($_POST["no_resep"]) ? validTeks4($_POST["no_resep"], 20) : "";
            if ($no_resep !== "") {
                bukaquery("delete from resep_dokter_racikan_detail where no_resep='" . cleankar2($no_resep) . "'");
                bukaquery("delete from resep_dokter_racikan where no_resep='" . cleankar2($no_resep) . "'");
                bukaquery("delete from resep_dokter where no_resep='" . cleankar2($no_resep) . "'");
                bukaquery("delete from resep_obat where no_resep='" . cleankar2($no_resep) . "' and kd_dokter='" . cleankar2($kd_dokter) . "'");
                $flash_ok = "Resep dihapus: " . dv_h($no_resep);
            }
        }
    }
}

// Queue for today (same as visit).
$queryQueue = bukaquery(
    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk," .
    "concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur) as umur,reg_periksa.stts,poliklinik.nm_poli " .
    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " .
    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli " .
    "where reg_periksa.kd_dokter='" . cleankar2($kd_dokter) . "' and reg_periksa.tgl_registrasi=current_date() " .
    "order by reg_periksa.no_reg asc"
);

$visit = null;
$resepList = null;
if ($no_rawat !== "") {
    $q = bukaquery(
        "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk," .
        "concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur) as umur,reg_periksa.stts,poliklinik.nm_poli,penjab.png_jawab " .
        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " .
        "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli " .
        "left join penjab on reg_periksa.kd_pj=penjab.kd_pj " .
        "where reg_periksa.no_rawat='" . cleankar2($no_rawat) . "' and reg_periksa.kd_dokter='" . cleankar2($kd_dokter) . "' and reg_periksa.tgl_registrasi=current_date() limit 1"
    );
    $visit = mysqli_fetch_assoc($q);
    if ($visit) {
        $resepList = bukaquery(
            "select no_resep,tgl_peresepan,jam_peresepan,status from resep_obat " .
            "where no_rawat='" . cleankar2($no_rawat) . "' and kd_dokter='" . cleankar2($kd_dokter) . "' " .
            "order by tgl_peresepan desc, jam_peresepan desc limit 20"
        );
    }
}

?>

<div class="doc-visit doc-resep">
    <div class="dv-hero">
        <div class="dv-hero__title">Peresepan Obat (Dokter)</div>
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
                    <a class="dv-queue__item<?=$active;?>" data-dv-queue-item data-dv-hay="<?=dv_h($hay);?>" href="index.php?act=Resep&no_rawat=<?=dv_h(urlencode($row["no_rawat"]));?>">
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
                        Belum ada pasien untuk Anda hari ini.
                    </div>
                <?php } ?>
            </div>
        </div>

        <div class="dv-panel">
            <div class="dv-panel__head">
                <h3>Resep</h3>
                <div class="dv-actions">
                    <a class="dv-btn" href="index.php?act=Visit&no_rawat=<?=dv_h(urlencode($no_rawat));?>">CPPT</a>
                    <a class="dv-btn" href="index.php?act=Pasien">Daftar Pasien</a>
                </div>
            </div>

            <?php if (!$visit) { ?>
                <div class="dv-section" style="color: var(--dv-muted);">
                    Pilih pasien dari panel kiri untuk membuat resep.
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
                    </div>
                </div>

                <div class="dv-panel__head">
                    <h3>Cari Obat</h3>
                </div>
                <div class="dv-section">
                    <input class="dv-input" type="text" placeholder="Ketik minimal 2 huruf: nama/kode obat" data-rx-search>
                    <div class="dv-divider"></div>
                    <div class="rx-results" data-rx-results></div>
                </div>

                <div class="dv-panel__head">
                    <h3>Draft Resep (Non Racikan)</h3>
                    <div class="dv-actions">
                        <button class="dv-btn" type="button" data-rx-add-manual>Tambah Manual</button>
                    </div>
                </div>
                <div class="dv-section">
                    <form method="post" autocomplete="off">
                        <input type="hidden" name="rx_action" value="save_resep">
                        <input type="hidden" name="no_rawat" value="<?=dv_h($visit["no_rawat"]);?>">

                        <div data-rx-rows></div>

                        <div class="dv-actions" style="margin-top: 12px;">
                            <button class="dv-btn dv-btn--primary" type="submit">Simpan Resep</button>
                            <label style="display:flex; align-items:center; gap:8px; margin:0; font-size: 12px; color: var(--dv-muted);">
                                <input type="checkbox" name="append_rtl" value="1">
                                Tambahkan ringkasan resep ke RTL CPPT terakhir
                            </label>
                        </div>
                    </form>
                </div>

                <div class="dv-panel__head">
                    <h3>Riwayat Resep (Terakhir)</h3>
                </div>
                <div class="dv-section">
                    <?php
                    $adaResep = false;
                    if ($resepList) {
                        while ($ro = mysqli_fetch_assoc($resepList)) {
                            $adaResep = true;
                            $itemsQ = bukaquery(
                                "select databarang.nama_brng,resep_dokter.jml,resep_dokter.aturan_pakai,databarang.kode_sat " .
                                "from resep_dokter inner join databarang on resep_dokter.kode_brng=databarang.kode_brng " .
                                "where resep_dokter.no_resep='" . cleankar2($ro["no_resep"]) . "' order by databarang.nama_brng"
                            );
                    ?>
                        <div class="dv-note" style="margin-bottom: 10px;">
                            <div class="dv-note__head">
                                <div><b><?=dv_h($ro["no_resep"]);?></b> · <?=dv_h($ro["tgl_peresepan"]);?> <?=dv_h($ro["jam_peresepan"]);?></div>
                                <div><?=dv_h($ro["status"]);?></div>
                            </div>
                            <div class="dv-note__body">
                                <ul style="margin: 0; padding-left: 18px;">
                                    <?php while ($it = mysqli_fetch_assoc($itemsQ)) { ?>
                                        <li><?=dv_h($it["nama_brng"]);?> (<?=dv_h($it["jml"]);?> <?=dv_h($it["kode_sat"]);?>) - <?=dv_h($it["aturan_pakai"]);?></li>
                                    <?php } ?>
                                </ul>
                                <div class="dv-actions" style="margin-top: 10px;">
                                    <form method="post" style="margin:0; display:inline;" onsubmit="return confirm('Hapus resep <?=dv_h($ro["no_resep"]);?> ?');">
                                        <input type="hidden" name="rx_action" value="delete_resep">
                                        <input type="hidden" name="no_rawat" value="<?=dv_h($visit["no_rawat"]);?>">
                                        <input type="hidden" name="no_resep" value="<?=dv_h($ro["no_resep"]);?>">
                                        <button class="dv-btn dv-btn--danger" type="submit">Hapus</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    <?php
                        }
                    }
                    ?>
                    <?php if (!$adaResep) { ?>
                        <div style="color: var(--dv-muted); font-size: 13px;">Belum ada resep untuk No.Rawat ini.</div>
                    <?php } ?>
                </div>
            <?php } ?>
        </div>
    </div>
</div>
