import { spawn, execFileSync } from 'node:child_process';
import { randomBytes } from 'node:crypto';
import { mkdirSync, writeFileSync, createWriteStream } from 'node:fs';
import { join } from 'node:path';
import { once } from 'node:events';
import assert from 'node:assert/strict';

// Somente banco novo e descartável dentro da pasta ignorada .tools.
mkdirSync('.tools', {recursive:true});
const id=randomBytes(8).toString('hex'), password=randomBytes(18).toString('hex');
const port=process.env.TEST_PORT||'8082', base='http://127.0.0.1:'+port;
const java=process.env.JAVA_HOME?join(process.env.JAVA_HOME,'bin',process.platform==='win32'?'java.exe':'java'):'java';
const db='jdbc:h2:file:./.tools/persistencia-'+id+';DB_CLOSE_ON_EXIT=FALSE;WRITE_DELAY=0';
let sourceSha=process.env.SOURCE_SHA||'TRABALHO_NAO_COMMITADO';
if(!process.env.SOURCE_SHA){try{if(!execFileSync('git',['status','--porcelain'],{encoding:'utf8'}).trim())sourceSha=execFileSync('git',['rev-parse','HEAD'],{encoding:'utf8'}).trim();}catch{}}
let child;
const auth={Authorization:'Basic '+Buffer.from('admin@exemplo.test:'+password).toString('base64')};
async function start(initial){
  child=spawn(java,['-jar','target/organizacao-recursos-1.0.0-SNAPSHOT.jar'],{
    windowsHide:true,env:{...process.env,PORT:port,BIND_ADDRESS:'127.0.0.1',DB_URL:db,DB_PASSWORD:'',ADMIN_EMAIL:'admin@exemplo.test',ADMIN_PASSWORD:initial?password:''},stdio:['ignore','pipe','pipe']});
  const log=createWriteStream('.tools/persistencia-'+id+'-'+(initial?'1':'2')+'.log');
  child.stdout.pipe(log);child.stderr.pipe(log);
  child.on('error',e=>console.error(e.message));
  for(let n=0;n<120;n++){
    if(child.exitCode!==null)throw new Error('JAR encerrou antes da prontidão. Consulte o log local.');
    try{const r=await fetch(base+'/api/saude',{signal:AbortSignal.timeout(1000)});if(r.ok)return;}catch{}
    await new Promise(r=>setTimeout(r,250));
  }
  throw new Error('Servidor não ficou pronto em 30 segundos.');
}
async function stop(){if(child&&child.exitCode===null){const ended=once(child,'exit');child.kill();await ended;}}
async function get(path){const r=await fetch(base+'/api'+path,{headers:auth});assert.equal(r.status,200);return r.json();}
try{
  // Rejeita porta já ocupada em vez de atingir aplicação alheia.
  let occupied=false;try{await fetch(base+'/api/saude',{signal:AbortSignal.timeout(500)});occupied=true;}catch{}
  assert.equal(occupied,false,'Porta ocupada: escolha TEST_PORT diferente.');
  await start(true);
  const csrfResponse=await fetch(base+'/api/csrf'),csrf=await csrfResponse.json();
  const headers={...auth,'Content-Type':'application/json',Cookie:csrfResponse.headers.getSetCookie().map(x=>x.split(';')[0]).join('; '),[csrf.headerName]:csrf.token};
  async function post(path,body){const r=await fetch(base+'/api'+path,{method:'POST',headers,body:JSON.stringify(body)});assert.equal(r.status,201);return r.json();}
  const resource=await post('/recursos',{nome:'Persistência sintética',tipo:'SALA',localizacao:'Teste descartável',capacidade:10,descricao:'Teste de reinício',ativo:true});
  const inicio=new Date(Date.now()+86400000*7).toISOString(),fim=new Date(Date.now()+86400000*7+3600000).toISOString();
  const booking=await post('/reservas',{recursoId:resource.id,inicio,fim,finalidade:'Persistência de reserva'});
  await stop();await start(false);
  assert.equal((await get('/recursos/'+resource.id)).nome,resource.nome);
  const bookings=await get('/reservas');assert.equal(bookings.length,1);assert.equal(bookings[0].id,booking.id);assert.equal(bookings[0].status,'ATIVA');
  const result={executadoEm:new Date().toISOString(),sourceSha,servidor:'JAR real em duas execuções',banco:'H2 em arquivo descartável com WRITE_DELAY=0',recursoPreservado:true,reservaPreservada:true,loginOriginalAposReinicio:true,reinicioSemSenhaBootstrap:true,aprovado:true};
  writeFileSync('docs/testes/persistencia-local.json',JSON.stringify(result,null,2)+'\n');console.log(JSON.stringify(result,null,2));
}finally{await stop();}
