
let dadosCache = ""





function LoadCards(){

	const CARD_CONTEINER = document.getElementById("prev-panel")


		const prevCards = `   
	<div class="prev-card">
		<div id="prev-header">

			<h3>${cidade.cidade}</h3>

			<div id="prev-01">
				<img src="" alt="">
				<p>23</p>
				<p>19</p>
			</div>

			<div id="">
				<p>12.2mm</p>
				<p>20km/h</p>
				<p>89%</p>
			</div>

		</div>

		<div class="prev-footer">
			<div>
				<img src="" alt="">
				<p>23</p>
				<p>19</p>
			</div>
			<div>
				<img src="" alt="">
				<p>23</p>
				<p>19</p>
			</div>
			<div>
				<img src="" alt="">
				<p>23</p>
				<p>19</p>
			</div>
		</div>
	</div>

	`



	}








function RequestTime(event) {
	event.preventDefault();

	const url = "/previsao/status"
	fetch(url)
		.then(res => res.text())
		.then(texto => {
			const horario = document.getElementById("horario").innerText = texto;
		})
}


function RequestPrev(event) {
	event.preventDefault();
	const popup = document.getElementById("loadingPopup");
	const acao = event.currentTarget.value;

	popup.showModal();

	RequestTime(event);

	const url = `/previsao/scrap?acao=${acao}`;

	fetch(url, { method: 'POST' })
		.then(resposta => resposta.json())
		.catch(erro => console.error("Erro no scrap:", erro.message))
		.finally(() => {

			popup.close();
		});
}

console.log(dadosCache.length);

window.onload = (event) => {
	RequestTime(event);
};

function getCache() {
	const url = "/previsao/cache"
	fetch(url)
		.then(res => res.json())
		.then(dados => {
			dadosCache = dados
		})

}