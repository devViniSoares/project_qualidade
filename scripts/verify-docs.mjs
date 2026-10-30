import { readdirSync, readFileSync, existsSync, statSync } from 'node:fs';
import { resolve, dirname, join } from 'node:path';
const root=resolve(import.meta.dirname,'..');
const files=[];
function walk(dir){for(const name of readdirSync(dir)){if(['.git','.tools','target','data','performance-local'].includes(name))continue;const p=join(dir,name);if(statSync(p).isDirectory())walk(p);else if(p.endsWith('.md'))files.push(p);}}
walk(root);let errors=[];
for(const path of files){const source=readFileSync(path,'utf8');let inside=false,mermaid=false;
 for(const line of source.split(/\r?\n/)){if(/^```/.test(line)){if(!inside&&!/^```\S+/.test(line))errors.push(path+': bloco sem linguagem');mermaid=!inside&&/^```mermaid/.test(line);inside=!inside;}else if(mermaid&&line.includes(';'))errors.push(path+': ponto e vírgula no Mermaid');}
 if(inside)errors.push(path+': bloco não fechado');
 for(const match of source.matchAll(/\[[^\]]*\]\(([^)]+)\)/g)){const href=match[1].split('#')[0];if(!href||/^(https?:|mailto:)/.test(href))continue;if(!existsSync(resolve(dirname(path),decodeURIComponent(href))))errors.push(path+': link ausente '+href);}
}
if(errors.length){console.error(errors.join('\n'));process.exit(1);}console.log(`${files.length} documentos verificados: links locais e blocos válidos.`);
