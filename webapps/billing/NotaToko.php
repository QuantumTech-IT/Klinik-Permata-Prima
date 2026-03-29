<?php
 include '../conf/conf.php';
?>
<html>
    <head>
        <link href="css/default.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <script type="text/javascript">
            window.onload = function() { window.print(); }
        </script>
        <?php
            reportsqlinjection();      
            $usere      = trim(isset($_GET['usere']))?trim($_GET['usere']):NULL;
            $passwordte = trim(isset($_GET['passwordte']))?trim($_GET['passwordte']):NULL;
            if((USERHYBRIDWEB==$usere)&&(PASHYBRIDWEB==$passwordte)){
                $nonota     = validTeks4(str_replace("_"," ",$_GET['nonota']),20); 
                $tanggal    = validTeks4($_GET['tanggal'],20); 
                $catatan    = validTeks4(str_replace("_"," ",$_GET['catatan']),70); 
                $petugas    = validTeks4(str_replace("_"," ",$_GET['petugas']),70); 
                $nomember   = validTeks4(str_replace("_"," ",$_GET['nomember']),20);
                $member     = validTeks4(str_replace("_"," ",$_GET['member']),70); 
                $besarppn   = validTeks4(str_replace("_"," ",$_GET['besarppn']),20);
                $ongkir     = validTeks4(str_replace("_"," ",$_GET['ongkir']),20);  
                $norawat    = getOne("select ifnull(no_rawat,'') from tokopenjualan where nota_jual='$nonota' limit 1");
                $notabilling= "";
                if($norawat!=""){
                    $notabilling = getOne("select ifnull(no_nota,'') from nota_jalan where no_rawat='$norawat' limit 1");
                }

                $_sql = "SELECT temporary_toko.no,temporary_toko.temp1,temporary_toko.temp2,temporary_toko.temp3,temporary_toko.temp4,temporary_toko.temp5,temporary_toko.temp6,temporary_toko.temp7, temporary_toko.temp8, temporary_toko.temp9, temporary_toko.temp10, temporary_toko.temp11, temporary_toko.temp12, temporary_toko.temp13 from temporary_toko order by temporary_toko.no asc";            
                $hasil=bukaquery($_sql);

                if(mysqli_num_rows($hasil)!=0) { 
                  $setting=  mysqli_fetch_array(bukaquery("select setting.nama_instansi,setting.alamat_instansi,setting.kabupaten,setting.propinsi,setting.kontak,setting.email,setting.logo from setting"));
                  echo "<table width='".getOne("select set_nota.notatoko from set_nota")."'  border='0' align='left' cellpadding='0' cellspacing='0' class='tbl_form'>
                         <tr class='isi14'>
                           <td width=50% colspan=4 align=left>
                               <table width='100%' bgcolor='#ffffff' padding='0' align='left' border='0' class='tbl_form'>
                                 <tr>
                                    <td padding='0'>
                                        <img width='60' height='60' src='data:image/jpeg;base64,". base64_encode($setting['logo']). "'/>
                                    </td>
                                    <td>
                                        <center>
                                            <font color='000000' size='3'  face='Tahoma'>".$setting["nama_instansi"]."</font><br>
                                            <font color='000000' size='2'  face='Tahoma'>
                                                ".$setting["alamat_instansi"].", ".$setting["kabupaten"].", ".$setting["propinsi"]."<br/>
                                                ".$setting["kontak"].", E-mail : ".$setting["email"]."
                                            </font> 
                                        </center>
                                    </td>
                                 </tr>
                               </table>
                           </td>
                         </tr>
                         <tr>
                             <td colspan='6'><hr/>
                                 <table width=100%>
                                     <tr class='isi14'>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>No.Member</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>: $nomember</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>No.Nota</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>: $nonota</font>
                                        </td>                                                                
                                     </tr> 
                                     <tr class='isi14'>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>Nama Member</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>: $member</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>Tanggal</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>: $tanggal</font>
                                        </td>                                                                
                                     </tr> 
                                     <tr class='isi14'>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>Alamat Member</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>: ".getOne(
                                                   "select alamat from tokomember where no_member='$nomember'")."</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>Petugas</font>
                                        </td>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>: $petugas</font>
                                        </td>                                                                
                                     </tr> 
                                     <tr class='isi14'>
                                        <td width='25%'>
                                           <font color='000000' size='2' face='Tahoma'>Catatan</font>
                                        </td>
                                        <td width='25%' colspan='3'>
                                           <font color='000000' size='2' face='Tahoma'>: $catatan</font>
                                        </td>                                                              
                                     </tr> 
                                 </table>
                             </td>
                         </tr>
                         <tr class='isi14'>
                               <td colspan=4 height='100%' valign=top>
                                    <table width=100% cellpadding='0' cellspacing='0'>
                                       <tr class=isi>
                                           <td width='5%' align=center><font color='000000' size='2'  face='Tahoma'>No</font></td>
                                           <td width='20%' align=center><font color='000000' size='2'  face='Tahoma'>Jml</font></td>
                                           <td width='55%' align=center><font color='000000' size='2'  face='Tahoma'>Nama Barang</font></td>
                                           <td width='20%' align=center><font color='000000' size='2'  face='Tahoma'>Total</font></td>
                                       </tr>";
                                              $ttlpesan=0;
                                              $i=1;
                                              while($barispesan = mysqli_fetch_array($hasil)) { 
                                                  $ttlpesan=$ttlpesan+$barispesan["temp10"];
                                                  echo "
                                                    <tr class='isi'>
                                                        <td><font color='000000' size='2'  face='Tahoma'>$i</font></td>
                                                        <td><font color='000000' size='2'  face='Tahoma'>$barispesan[1] $barispesan[5]</font></td>
                                                        <td><font color='000000' size='2'  face='Tahoma'>$barispesan[3] </font></td>
                                                        <td align=right><font color='000000' size='2'  face='Tahoma'>".formatDuit2($barispesan["temp10"])."</font></td>
                                                   </tr>";$i++;
                                              }    
                                     echo " <tr class='isi14'>
                                              <td colspan=2></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>Jumlah Total</font></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>".formatDuit2($ttlpesan)."</font></td>
                                              <td></td>
                                            </tr>  
                                            <tr class='isi14'>
                                              <td colspan=2></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>PPN</font></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>".formatDuit2($besarppn)."</font></td>
                                              <td></td>
                                            </tr>  
                                            <tr class='isi14'>
                                              <td colspan=2></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>Ongkir</font></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>".formatDuit2($ongkir)."</font></td>
                                              <td></td>
                                            </tr>  
                                            <tr class='isi14'>
                                              <td colspan=2></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>Jumlah Total+Ongkir+PPN</font></td>
                                              <td align='right'><font color='000000' size='2'  face='Tahoma'>".formatDuit2($ttlpesan+$besarppn)."</font></td>
                                              <td></td>
                                            </tr>";
                                    if($norawat!=""){
                                        $_sqlbilling = "select billing.nm_perawatan,billing.totalbiaya,billing.biaya,billing.jumlah,billing.tambahan,billing.status ".
                                                       "from billing where billing.no_rawat='$norawat' ".
                                                       "and billing.status not in ('-','Dokter','Perawat','Tagihan','TtlObat','TtlLaborat','TtlOperasi','TtlRadiologi','TtlTambahan','TtlPotongan','TtlRalan Dokter','TtlRalan Paramedis','TtlRalan Dokter Paramedis','TtlKamar','TtlRetur Obat','TtlResep Pulang') ".
                                                       "and (billing.totalbiaya<>0 or billing.biaya<>0 or billing.jumlah<>0 or billing.tambahan<>0) ".
                                                       "order by billing.noindex asc";
                                        $hasilbilling=bukaquery($_sqlbilling);
                                        if(mysqli_num_rows($hasilbilling)!=0){
                                            echo "<tr class='isi14'>
                                                    <td colspan='4'><hr/>
                                                        <table width='100%' cellpadding='0' cellspacing='0'>
                                                            <tr class='isi14'>
                                                                <td colspan='3' align='left'><font color='000000' size='2' face='Tahoma'><b>Rincian Billing</b></font></td>
                                                            </tr>
                                                            <tr class='isi14'>
                                                                <td width='50%'><font color='000000' size='2' face='Tahoma'>No.Rawat : $norawat</font></td>
                                                                <td width='50%' colspan='2' align='right'><font color='000000' size='2' face='Tahoma'>No.Nota Billing : ".($notabilling==""?"-":$notabilling)."</font></td>
                                                            </tr>
                                                            <tr class='isi'>
                                                                <td width='5%' align='center'><font color='000000' size='2' face='Tahoma'>No</font></td>
                                                                <td width='75%' align='left'><font color='000000' size='2' face='Tahoma'>Item Billing</font></td>
                                                                <td width='20%' align='right'><font color='000000' size='2' face='Tahoma'>Total</font></td>
                                                            </tr>";
                                            $i2=1;
                                            $ttlbilling=0;
                                            while($barisbilling = mysqli_fetch_array($hasilbilling)) {
                                                $ttlbilling = $ttlbilling + $barisbilling["totalbiaya"];
                                                echo "<tr class='isi'>
                                                        <td><font color='000000' size='2' face='Tahoma'>$i2</font></td>
                                                        <td><font color='000000' size='2' face='Tahoma'>".$barisbilling["nm_perawatan"]."</font></td>
                                                        <td align='right'><font color='000000' size='2' face='Tahoma'>".formatDuit2($barisbilling["totalbiaya"])."</font></td>
                                                      </tr>";
                                                $i2++;
                                            }
                                            echo "          <tr class='isi14'>
                                                                <td colspan='2' align='right'><font color='000000' size='2' face='Tahoma'><b>Total Billing</b></font></td>
                                                                <td align='right'><font color='000000' size='2' face='Tahoma'><b>".formatDuit2($ttlbilling)."</b></font></td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                  </tr>";
                                        }
                                    }
                                    echo "  
                                  </table>
                              </td>
                            </tr>
                         </table>";
                } else {echo "<b>Data pesan masih kosong !</b>";}
            }else {
                exit(header("Location:../index.php"));
            }
        ?>
    </body>
</html>
